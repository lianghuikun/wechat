package com.wechat.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.domain.BaseMessage;
import com.wechat.domain.EventMessage;
import com.wechat.domain.Knowledge;
import com.wechat.domain.TextMessage;
import com.wechat.enums.MsgTypeEnum;
import com.wechat.enums.ReqTypeEnum;
import com.wechat.robot.TuringRobotUtil;
import com.wechat.service.KnowledgeService;
import com.wechat.util.MessageUtil;
import com.wechat.util.SHA1Util;
import com.wechat.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 消息-》微信服务器-》微信服务器通过配置的回调地址-》调用我的服务器-》我的服务器返回给微信服务器-。。。
 */
@RestController
@RequestMapping("/wechatHandler")
public class WechatController {

    private static Logger logger = LoggerFactory.getLogger(WechatController.class);
    private static final String WECHAT_TOKEN = "KING";
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * 微信服务器调用我们服务器看是否能调通
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping
    public String checkSignature(String signature,
                                 String timestamp,
                                 String nonce,
                                 String echostr) {

        Boolean flag = check(signature,
                timestamp,
                nonce);
        if (flag) {
            return echostr;
        }
        return "abc";
    }


    /**
     * 校验签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public Boolean check(String signature,
                         String timestamp,
                         String nonce) {

        // 1、将token   timestamp   nonce 按字典序排列，也就是按照字母排序
        List<String> list = Arrays.asList(WECHAT_TOKEN, timestamp, nonce);
        logger.info("---排序前---->:" + JSONObject.toJSONString(list));
        Collections.sort(list);
        logger.info("---排序后---->:" + JSONObject.toJSONString(list));
        // 2、需要将排序好的三个字符串拼接
        String str = "";
        for (String s : list) {
            str += s;
        }
        // 3、将拼接好的字符串进行SHA1加密
        String realSignature = SHA1Util.encode(str);
        if (signature.equals(realSignature)) {
            /*
             *  如果校验成功，直接返回
             *  校验的目的是，确认是微信发过来的消息
             */
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 回复微信消息，切记微信是POST请求
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping
    public String receive(HttpServletRequest request, HttpServletResponse response) {
        // 来源验证
        if (checkIsAllow(request))
            return null;
        Map<String, String> pool = null;
        try {
            pool = MessageUtil.parseXml(request);
        } catch (Exception e) {
            logger.info("数据转换异常.");
            e.printStackTrace();
            return null;
        }
        String json = JSON.toJSONString(pool);
        BaseMessage baseMessage = JSON.parseObject(json, BaseMessage.class);
//            TextMessage textMessage = JSON.parseObject(json, TextMessage.class);
        String msgType = baseMessage.getMsgType();
        if (msgType.equals(MsgTypeEnum.TEXT.getCode())) {
            TextMessage textMessage = JSON.parseObject(json, TextMessage.class);
            // 如果是文本消息
            String sql = textMessage.getContent();
            String reply = null;
            try {
                reply = TuringRobotUtil.query(ReqTypeEnum.TEXT.getCode(), UUIDUtil.getId(), sql);
                logger.info("--图灵机器人回复-->:" + reply);
                Knowledge knowledge = TuringRobotUtil.getResult(reply, sql);
                executorService.submit(() -> {
                    knowledgeService.save(knowledge);
                });
                return replyTextMsg(textMessage.getFromUserName(), textMessage.getToUserName(), knowledge.getReply());
            } catch (IOException e) {
                logger.info("调用图灵机器人异常。。。。" + e.getMessage());
                // 朕今天回复太多了，不想说话了。。。
                reply = "朕今天回复太多了，不想说话了。。。,想要和朕自动聊天，明天再来吧。。。";
                return replyTextMsg(textMessage.getFromUserName(), textMessage.getToUserName(), reply);
            }
        } else if (msgType.equals("event")) {
            // 如果是事件，那么就是 关注和 取消,   事件类型，subscribe(订阅)、unsubscribe(取消订阅)
            EventMessage eventMessage = JSON.parseObject(json, EventMessage.class);
            String event = eventMessage.getEvent();
            if ("subscribe".equals(event)) {
                // 如果是订阅，还有可能是扫描
                String eventKey = eventMessage.getEventKey();
                if (StringUtils.isNotBlank(eventKey)) {
                    // 如果是扫描
                    // EventKey	事件KEY值，qrscene_为前缀，后面为二维码的参数值
                    // 获取去掉前缀 qrscene_的值,获取长净值
                    String sceneId = eventKey.substring(8, eventKey.length());
                    // 返回消息，可以给用户提供场景值如果有必要的话
                    return null;
                } else {
                    // 如果仅仅是订阅公众号
                    String content = "哇偶，等于等到你，怎么此时才来订阅啊，拉出去打哭...";
                    // 如果是订阅,可以发送文本 语音 视频等，自己定义
                    // 如果是订阅发送文字
                    TextMessage textMessage = new TextMessage();
                    TextMessage rspMessage = new TextMessage();
                    rspMessage.setToUserName(eventMessage.getFromUserName());
                    rspMessage.setFromUserName(eventMessage.getToUserName());
                    // 当前时间戳
                    rspMessage.setCreateTime(Instant.now().toEpochMilli());
                    rspMessage.setMsgType("text");
                    rspMessage.setContent(content);
                    String writeXml = MessageUtil.textMessageToXml(rspMessage);
                    return writeXml;
                }
            } else if ("unsubscribe".equals(event)) {
                // 如果是unsubscribe 取消订阅
                String content = "你取消了啊，我在你后面看着你呢...";
                // 如果是订阅,可以发送文本 语音 视频等，自己定义
                // 如果是订阅发送文字
                TextMessage textMessage = new TextMessage();
                TextMessage rspMessage = new TextMessage();
                rspMessage.setToUserName(eventMessage.getFromUserName());
                rspMessage.setFromUserName(eventMessage.getToUserName());
                // 当前时间戳
                rspMessage.setCreateTime(Instant.now().toEpochMilli());
                rspMessage.setMsgType("text");
                rspMessage.setContent(content);
                String writeXml = MessageUtil.textMessageToXml(rspMessage);
                return writeXml;
            }
        }
        return null;
    }

    /**
     * 回复文本信息
     *
     * @param reply
     * @param fromUserName
     * @param toUserName
     * @return
     */
    private String replyTextMsg(String fromUserName, String toUserName, String reply) {
        // 将文本消息原样返回做为回复
        TextMessage rspMessage = new TextMessage();
        // 交换接收和发送双方的身份
        rspMessage.setToUserName(fromUserName);
        rspMessage.setFromUserName(toUserName);
        // 当前时间戳
        rspMessage.setCreateTime(Instant.now().toEpochMilli());
        rspMessage.setMsgType(MsgTypeEnum.TEXT.getCode());
        rspMessage.setContent(reply);
        // msgId不需要设置
//                rspMessage.setMsgId();
        return MessageUtil.textMessageToXml(rspMessage);
    }

    /**
     * 校验签名，是不是微信发送过来的消息
     *
     * @param request
     * @return
     */
    private boolean checkIsAllow(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        // 切记 post请求中 不含有echostr， get请求可以直接获得
        // String echostr =  request.getAttribute("echostr");
        Boolean flag = check(signature,
                timestamp,
                nonce);
        if (!flag) {
            return true;
        }
        return false;
    }
}
