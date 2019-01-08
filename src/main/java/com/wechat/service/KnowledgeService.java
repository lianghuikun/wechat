package com.wechat.service;


import com.wechat.domain.Knowledge;
import sun.reflect.generics.tree.VoidDescriptor;

import java.util.List;

public interface KnowledgeService {
    List<Knowledge> getKnowledgeList();

    void save(Knowledge knowledge);

    void update(Knowledge knowledge);

    void deleteById(Integer id);
}
