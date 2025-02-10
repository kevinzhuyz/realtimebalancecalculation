package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.model.TransactionType;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.repository.TransactionRepository;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private CacheService cacheService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction mockTransaction;
    private Account mockSourceAccount;
    private Account mockTargetAccount;
    private CreateTransactionRequest mockRequest;

    @BeforeEach
    void setUp() {
        // 设置 RedisTemplate mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true);

        // 设置 CacheService mock
        when(cacheService.get(anyString(), any())).thenReturn(null);

        // 准备交易数据
        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setType(TransactionType.TRANSFER);
        mockTransaction.setDescription("测试交易");
        mockTransaction.setTransactionTime(LocalDateTime.now());

        // 准备账户数据
        mockSourceAccount = new Account();
        mockSourceAccount.setId(1L);
        mockSourceAccount.setBalance(new BigDecimal("1000.00"));

        mockTargetAccount = new Account();
        mockTargetAccount.setId(2L);
        mockTargetAccount.setBalance(new BigDecimal("500.00"));

        // 准备请求数据
        mockRequest = new CreateTransactionRequest();
        mockRequest.setSourceAccountId(1L);
        mockRequest.setTargetAccountId(2L);
        mockRequest.setAmount(new BigDecimal("100.00"));
        mockRequest.setType(TransactionType.TRANSFER);
        mockRequest.setDescription("测试交易");
    }

    @Test
    void createTransaction_TransferSuccess() {
        // 准备
        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(mockTargetAccount));
        when(transactionMapper.toTransaction(any())).thenReturn(mockTransaction);
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenReturn(mockTransaction);

        // 执行
        Transaction result = transactionService.createTransaction(mockRequest);

        // 验证
        assertNotNull(result);
        assertEquals(TransactionType.TRANSFER, result.getType());
        verify(accountRepository, times(2)).save(any());
        assertEquals(new BigDecimal("900.00"), mockSourceAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), mockTargetAccount.getBalance());
    }

    @Test
    void createTransaction_SourceAccountNotFound() {
        // 准备
        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, () -> transactionService.createTransaction(mockRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createTransaction_TargetAccountNotFound() {
        // 准备
        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, () -> transactionService.createTransaction(mockRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getTransactionsByAccountId_Success() {
        // 准备
        when(transactionRepository.findBySourceAccountIdOrTargetAccountId(1L, 1L))
                .thenReturn(Arrays.asList(mockTransaction));

        // 执行
        List<Transaction> results = transactionService.getTransactionsByAccountId(1L);

        // 验证
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(TransactionType.TRANSFER, results.get(0).getType());
    }

    @Test
    void getTransactionById_Success() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        // 执行
        Transaction result = transactionService.getTransactionById(1L);

        // 验证
        assertNotNull(result);
        assertEquals(TransactionType.TRANSFER, result.getType());
    }

    @Test
    void getTransactionById_NotFound() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(1L));
    }
} 