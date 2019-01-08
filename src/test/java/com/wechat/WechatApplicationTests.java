package com.wechat;

import com.alibaba.fastjson.JSON;
import com.wechat.dao.KnowledgeDao;
import com.wechat.domain.Knowledge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WechatApplicationTests {

    @Autowired
    private KnowledgeDao knowledgeDao;
    @Test
    public void contextLoads() {

        List<Knowledge> all = knowledgeDao.findAll();
        System.out.println("-------->:" + JSON.toJSONString(all));
    }

}

