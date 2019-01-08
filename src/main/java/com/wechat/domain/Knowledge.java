package com.wechat.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 知识库
 */
@Data
@Entity
public class Knowledge {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 问题
     */
    private String query;
    /**
     * 回复
     */
    private String reply;
}
