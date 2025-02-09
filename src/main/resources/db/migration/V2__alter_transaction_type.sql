ALTER TABLE transactions 
MODIFY COLUMN type VARCHAR(20) NOT NULL COMMENT '交易类型: DEPOSIT,WITHDRAW,TRANSFER,PAYMENT,REFUND';

UPDATE transactions SET type = 'DEPOSIT' WHERE type = 'deposit';
UPDATE transactions SET type = 'WITHDRAW' WHERE type = 'withdraw';
UPDATE transactions SET type = 'TRANSFER' WHERE type = 'transfer';
UPDATE transactions SET type = 'PAYMENT' WHERE type = 'payment';
UPDATE transactions SET type = 'REFUND' WHERE type = 'refund'; 