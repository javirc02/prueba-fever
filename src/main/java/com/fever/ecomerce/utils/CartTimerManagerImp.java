package com.fever.ecomerce.utils;

import com.fever.ecomerce.storage.LocalStorage;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class CartTimerManagerImp implements CartTimerManager {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();
    private final LocalStorage localStorage;

    private static final long EXPIRATION_TIME_MINUTES = 10;

    public CartTimerManagerImp(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public void scheduleCartExpiration(String cartId, Runnable onExpire) {
        cancelTimer(cartId);

        ScheduledFuture<?> future = executor.schedule(() -> {
            localStorage.deleteCartById(cartId);
            timers.remove(cartId);
            System.out.println("Cart " + cartId + " expired and was deleted.");
            onExpire.run();
        }, EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);

        timers.put(cartId, future);
    }

    @Override
    public void cancelTimer(String cartId) {
        ScheduledFuture<?> future = timers.remove(cartId);
        if (future != null) {
            future.cancel(false);
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}
