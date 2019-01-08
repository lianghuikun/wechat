package com.wechat.dao;

import com.wechat.domain.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeDao extends JpaRepository<Knowledge, Integer> {
}
