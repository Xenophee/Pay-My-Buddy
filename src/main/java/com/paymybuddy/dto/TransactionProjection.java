package com.paymybuddy.dto;


public interface TransactionProjection {
    String getUsername();
    boolean getIsSender();
    Double getAmount();
    String getDescription();
    String getProcessedAt();
}
