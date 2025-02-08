package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.repository.TransactionRepository;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction mockTransaction;
    private Account mockSourceAccount;
    private Account mockTargetAccount;
    private CreateTransactionRequest mockRequest;

    @BeforeEach
    void setUp() {
        // 初始化 mock 账户
        mockSourceAccount = new Account();
        mockSourceAccount.setId(1L);
        mockSourceAccount.setBalance(new BigDecimal("1000.00"));

        mockTargetAccount = new Account();
        mockTargetAccount.setId(2L);
        mockTargetAccount.setBalance(new BigDecimal("500.00"));

        // 初始化 mock 交易
        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setType("TRANSFER");

        // 初始化 mock 请求
        mockRequest = new CreateTransactionRequest();
        mockRequest.setSourceAccountId(1L);
        mockRequest.setTargetAccountId(2L);
        mockRequest.setAmount(new BigDecimal("100.00"));
        mockRequest.setType("TRANSFER");
    }

    @Test
    void createTransaction_TransferSuccess() {
        // 准备
        // 设置验证用的 mock
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(mockTargetAccount));
        
        // 设置执行用的 mock
        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(mockTargetAccount));
        when(transactionMapper.toTransaction(any(CreateTransactionRequest.class))).thenReturn(mockTransaction);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // 执行
        Transaction result = transactionService.createTransaction(mockRequest);

        // 验证
        assertNotNull(result);
        assertEquals(mockTransaction.getId(), result.getId());
        verify(accountRepository, times(2)).save(any(Account.class));
        
        // 验证余额变化
        assertEquals(new BigDecimal("900.00"), mockSourceAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), mockTargetAccount.getBalance());
    }

    @Test
    void createTransaction_SourceAccountNotFound() {
        // 准备
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, () -> transactionService.createTransaction(mockRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).findByIdWithLock(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getTransactionsByAccountId_Success() {
        // 准备
        List<Transaction> mockTransactions = Arrays.asList(mockTransaction);
        when(transactionRepository.findBySourceAccountIdOrTargetAccountId(1L, 1L))
                .thenReturn(mockTransactions);

        // 执行
        List<Transaction> results = transactionService.getTransactionsByAccountId(1L);

        // 验证
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(mockTransaction.getId(), results.get(0).getId());
    }

    @Test
    void getTransactionById_Success() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        // 执行
        Transaction result = transactionService.getTransactionById(1L);

        // 验证
        assertNotNull(result);
        assertEquals(mockTransaction.getId(), result.getId());
    }

    @Test
    void getTransactionById_NotFound() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void getAllTransactions_Success() {
        // 准备
        List<Transaction> mockTransactions = Arrays.asList(mockTransaction);
        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        // 执行
        List<Transaction> results = transactionService.getAllTransactions();

        // 验证
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(mockTransaction.getId(), results.get(0).getId());
    }
} 