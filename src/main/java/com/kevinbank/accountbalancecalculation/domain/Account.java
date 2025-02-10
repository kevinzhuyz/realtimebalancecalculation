/**
 * 包含账户余额计算相关的领域模型。
 */
package com.kevinbank.accountbalancecalculation.domain;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Account 类表示用户的账户信息，包括卡号、用户ID、交易金额、账户余额、信用额度和账户号码。
 * 使用 Lombok 的 @Data 注解自动生成 getter 和 setter 方法，简化代码并提高可维护性。
 */
@Data
public class Account {
    /**
     * 银行卡的唯一标识符。
     */
    private Long cardId;

    /**
     * 用户的唯一标识符。
     */
    private Long userId;

    /**
     * 交易或查询涉及的金额。
     */
    private BigDecimal amount;

    /**
     * 用户账户的当前余额。
     */
    private BigDecimal balance;

    /**
     * 账户的信用额度，表示最大透支限额。
     */
    private BigDecimal creditLimit;

    /**
     * 账户的唯一号码。
     */
    private String accountNumber;
}
