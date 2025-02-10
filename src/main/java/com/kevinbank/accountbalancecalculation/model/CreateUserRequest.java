/**
 * 用户创建请求模型类
 * 用于处理用户创建时的请求数据，确保数据的合法性
 */
package com.kevinbank.accountbalancecalculation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户创建请求的数据模型
 * 该类使用@Data注解来自动生成getter和setter方法，简化代码
 */
@Data
public class CreateUserRequest {
    /**
     * 用户名字段，必须填写且长度在3到50个字符之间
     * 这是为了确保用户名具有足够的复杂度，同时避免过长的输入
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String name;

    /**
     * 密码字段，必须填写且长度在6到100个字符之间
     * 这样可以确保密码的安全性，同时避免因密码过长而导致的存储问题
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    /**
     * 性别字段，非必填
     * 提供性别信息，允许用户选择是否填写
     */
    private String gender;
}
