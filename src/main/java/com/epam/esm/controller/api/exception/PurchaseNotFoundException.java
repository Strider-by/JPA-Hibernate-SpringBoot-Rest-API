package com.epam.esm.controller.api.exception;

public class PurchaseNotFoundException extends RuntimeException {

    private final long purchaseId;

    public PurchaseNotFoundException(long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public long getPurchaseId() {
        return purchaseId;
    }

}
