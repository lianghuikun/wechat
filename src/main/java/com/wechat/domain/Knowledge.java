package com.wechat.domain;

import lombok.Data;

import javax.persistence.*;

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
    @Column(length = 512)
    private String query;
    /**
     * 回复
     */
    @Column(length = 1024)
    private String reply;
    /**
     * 类型
     */
    @Column(length = 32)
    private String type;
}
