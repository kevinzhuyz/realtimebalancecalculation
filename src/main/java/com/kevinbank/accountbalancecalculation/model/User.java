package com.kevinbank.accountbalancecalculation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 用户实体类，用于在数据库中存储用户信息
 * 该类使用了Lombok的@Data注解，自动生成getter和setter方法，以及构造函数等
 * 使用JPA的@Entity注解，表明这是一个JPA实体类
 */
@Data
@Entity
@Table(name = "users")
@JsonSerialize
public class User {
    /**
     * 用户ID，主键，由数据库自动生成
     * 使用@GeneratedValue(strategy = GenerationType.IDENTITY)注解，表示这是一个自动递增的主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize
    private Long id;

    /**
     * 用户名，唯一且不能为空
     * 使用@Column(unique = true, nullable = false)注解，表示这是一个唯一的、不允许为空的列
     */
    @Column(unique = true, nullable = false)
    @JsonSerialize
    private String name;

    /**
     * 用户密码的哈希值，不能为空
     * 使用@Column(nullable = false)注解，表示这是一个不允许为空的列
     * 密码通常以哈希值形式存储，以增加安全性
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * 用户性别，可以为空
     * 没有使用注解，因为这是一个可选字段，允许为空
     */
    private String gender;

    /**
     * 用户创建时间，存储在数据库中的列名为created_at
     * 使用@Column(name = "created_at")注解，指定数据库中的列名
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 在用户实体被持久化到数据库之前，设置创建时间为当前时间
     * 使用@PrePersist注解，表示在实体被持久化之前执行该方法
     * 这样可以自动记录用户实体被创建的时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
