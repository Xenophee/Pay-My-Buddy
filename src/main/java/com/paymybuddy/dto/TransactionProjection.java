package com.paymybuddy.dto;

import java.time.LocalDateTime;

public interface TransactionProjection {
    String getUsername();
    boolean isSender();
    Double getAmount();
    String getDescription();
    LocalDateTime getProcessedAt();
}
