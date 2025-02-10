/**
 * AccountService接口定义了账户相关操作的服务方法
 * 包括账户创建、余额更新、账户查询以及资金操作等功能
 */
package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * AccountService接口，提供账户业务逻辑的方法定义
 */
public interface AccountService {

    /**
     * 创建新账户
     *
     * @param request 包含创建账户所需信息的请求对象
     * @return 返回新创建的Account对象
     */
    Account createAccount(CreateAccountRequest request);

    /**
     * 更新账户余额
     *
     * @param accountId 账户ID
     * @param amount 更新的金额，可以是存款也可以是取款
     */
    void updateBalance(Long accountId, BigDecimal amount);

    /**
     * 根据账户ID获取账户信息
     *
     * @param id 账户ID
     * @return 返回对应的Account对象，如果找不到则返回null
     */
    Account getAccountById(Long id);

    /**
     * 获取所有账户信息
     *
     * @return 返回账户列表
     */
    List<Account> getAllAccounts();

    /**
     * 向指定账户存款
     *
     * @param accountId 账户ID
     * @param amount 存款金额
     * @return 返回更新后的Account对象
     */
    Account deposit(Long accountId, BigDecimal amount);

    /**
     * 从指定账户取款
     *
     * @param accountId 账户ID
     * @param amount 取款金额
     * @return 返回更新后的Account对象
     */
    Account withdraw(Long accountId, BigDecimal amount);

    /**
     * 在两个账户之间转账
     *
     * @param sourceAccountId 转出账户ID
     * @param targetAccountId 转入账户ID
     * @param amount 转账金额
     */
    void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount);
}
