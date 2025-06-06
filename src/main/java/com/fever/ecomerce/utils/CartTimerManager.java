package com.fever.ecomerce.utils;

public interface CartTimerManager {
    void scheduleCartExpiration(String cartId, Runnable onExpire);
    void cancelTimer(String cartId);
}
