package com.wechat.robot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wechat.constant.ConstantCode;
import com.wechat.domain.Knowledge;
import com.wechat.enums.ReqTypeEnum;
import okhttp3.*;

import java.io.IOException;

/**
 * 图灵机器人
 */
public class TuringRobotUtil {
    private static final String ROBOT_URL = "http://openapi.tuling123.com/openapi/api/v2";
    private static final String API_KEY = "f9e17b44c5254f98856ee3de6f09a363";
    private static OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    /**
     * 调用图灵机器人
     *
     * @param reqType 输入类型:0-文本(默认)、1-图片、2-音频
     * @param userId  用户位移标识
     */
    public static String query(Integer reqType, String userId, String sql) throws IOException {
        JSONObject request = new JSONObject();
        request.put("reqType", reqType);
        JSONObject perception = new JSONObject();

        if (ReqTypeEnum.TEXT.getCode().intValue() == reqType) {
            JSONObject inputText = new JSONObject();
            inputText.put("text", sql);
            perception.put("inputText", inputText);

        } else if (ReqTypeEnum.PICTURE.getCode().intValue() == reqType) {
            JSONObject inputImage = new JSONObject();
            inputImage.put("url", sql);
            perception.put("inputImage", inputImage);

        } else if (ReqTypeEnum.MEDIA.getCode().intValue() == reqType) {
            JSONObject inputMedia = new JSONObject();
            inputMedia.put("url", sql);
            perception.put("inputMedia", inputMedia);

        }
        request.put("perception", perception);
        JSONObject userInfo = new JSONObject();
        userInfo.put("apiKey", API_KEY);
        userInfo.put("userId", userId);
        request.put("userInfo", userInfo);

        System.out.println("---------->:" + request);

        RequestBody body = RequestBody.create(JSON, request.toJSONString());
        Request req = new Request.Builder()
                .url(ROBOT_URL)
                .post(body)
                .build();
        try (Response response = client.newCall(req).execute()) {
            String string = response.body().string();

            // {"intent":{"code":4001},"results":[{"groupType":0,"resultType":"text","values":{"text":"加密方式错误!"}}]}
            System.out.println("----------->:" + string);
            return string;
            // {"emotion":{"robotEmotion":{"a":0,"d":0,"emotionId":0,"p":0},"userEmotion":{"a":0,"d":0,"emotionId":21500,"p":0}},"intent":{"actionName":"","code":10004,"intentName":""},"results":[{"groupType":1,"resultType":"text","values":{"text":"还不错，你呢"}}]}
        }
    }

    /**
     * 获取结果
     *
     * @param rspStr
     * @return
     */
    public static Knowledge getResult(String rspStr, String sql) {
        JSONObject json = JSONObject.parseObject(rspStr);
        JSONObject intent = json.getJSONObject("intent");
        JSONArray results = json.getJSONArray("results");
        Integer code = intent.getInteger("code");
        JSONObject result = (JSONObject) results.get(0);
        /**
         *          resultType
         *              文本(text);
         *              连接(url);
         *              音频(voice);
         *              视频(video);
         *              图片(image);
         *              图文(news)
         *         MsgType
         *              text        文本
         *              image       图片
         *              voice       语音
         *              video       视频
         *              shortvideo  小视频
         *              location    地理信息
         *              link        链接
         */
        String resultType = result.getString("resultType");
        JSONObject values = result.getJSONObject("values");
        String text = values.getString("text");
        Knowledge knowledge = new Knowledge();
        knowledge.setQuery(sql);
        knowledge.setReply(text);
        knowledge.setType(resultType);
        return knowledge;
    }

    public static void main(String[] args) {
        Integer reqType = 0;
        String userId = "21100880";
        String sql = "你好吗?";
        try {
//            String query = query(reqType, userId, sql);

            String  query = "{\"intent\":{\"actionName\":\"\",\"code\":10013,\"intentName\":\"\"},\"results\":[{\"groupType\":1,\"resultType\":\"text\",\"values\":{\"text\":\"羿，帝尧时期人物，嫦娥的丈夫，被帝尧封于商丘。他善于射箭，曾经帮助尧帝射下九日。只留一日，给大地带来复苏的生机，人们尊称他为“大羿”。\"}}]}";


            Knowledge knowledge = getResult(query, sql);
            System.out.println("--->:" + JSONObject.toJSONString(knowledge));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
