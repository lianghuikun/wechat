package com.wechat.service.impl;

import com.wechat.dao.KnowledgeDao;
import com.wechat.domain.Knowledge;
import com.wechat.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    @Autowired
    private KnowledgeDao knowledgeDao;
    @Override
    public List<Knowledge> getKnowledgeList() {
        return knowledgeDao.findAll();
    }

    @Override
    public void save(Knowledge knowledge) {
        knowledgeDao.save(knowledge);
    }

    @Override
    public void update(Knowledge knowledge) {
        knowledgeDao.saveAndFlush(knowledge);
    }

    @Override
    public void deleteById(Integer id) {
        knowledgeDao.deleteById(id);
    }
}
