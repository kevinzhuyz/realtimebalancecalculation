package com.kevinbank.accountbalancecalculation.model;

import lombok.Getter;
import lombok.Setter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Account 类表示用户账户信息及其相关操作。
 * 包含用户的基本信息，如卡号、姓名、密码哈希值、性别、余额、信用额度和部门。
 * 使用 Lombok 的 @Getter 自动生成字段的 getter 方法，使用 @Setter 自动生成字段的 setter 方法。
 */
@Getter
public class Account {

    @Setter
    private String cardId; // 用户卡号

    @Setter
    private String userName; // 用户姓名

    @Setter
    private String passwordHash; // 密码哈希值

    @Setter
    private char sex; // 性别

    private double money; // 余额

    @Setter
    private double limit; // 信用额度
    @Setter
    private String part; // 部门

    /**
     * 默认构造函数。
     */
    public Account() {}

    /**
     * 设置用户的密码，并自动将其转换为哈希值进行存储。
     * @param password 要设置的密码
     */
    public void setPassword(String password) {
        this.passwordHash = hashPassword(password);
    }

    /**
     * 验证用户的密码。
     * @param password 要验证的密码
     * @return 如果密码正确，返回 true；否则返回 false
     */
    public boolean checkPassword(String password) {
        if (this.passwordHash == null) {
            return false;
        }
        return this.passwordHash.equals(hashPassword(password));
    }

    /**
     * 对密码进行哈希处理。
     * 使用 SHA-256 算法将密码转换为哈希值。
     * @param password 要哈希的密码
     * @return 密码的哈希值
     * @throws RuntimeException 如果哈希算法不可用，抛出运行时异常
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * 设置用户的余额，该方法是同步的，以确保线程安全。
     * @param money 要设置的余额
     */
    public synchronized void setMoney(double money) {
        this.money = money;
    }

}
