package com.carbon.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class AsyncTaskService {

    private final Executor asyncExecutor;

    public AsyncTaskService(@Qualifier("asyncExecutor") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    /**
     * 执行无返回值的异步任务
     *
     * @param task 要执行的任务
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, asyncExecutor);
    }

    /**
     * 执行有返回值的异步任务
     *
     * @param supplier 返回结果的供应商
     * @return CompletableFuture<T>
     */
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, asyncExecutor);
    }

    /**
     * 异步执行任务并消费结果
     *
     * @param supplier 结果供应商
     * @param consumer 结果消费者
     * @return CompletableFuture<Void>
     */
    public <T> CompletableFuture<Void> supplyAsyncThenAccept(Supplier<T> supplier, Consumer<T> consumer) {
        return CompletableFuture
                .supplyAsync(supplier, asyncExecutor)
                .thenAcceptAsync(consumer, asyncExecutor); // 确保后续操作也在同一线程池
    }

    /**
     * 异步执行任务并转换结果
     *
     * @param supplier 结果供应商
     * @param mapper   结果转换器
     * @return CompletableFuture<U>
     */
    public <T, U> CompletableFuture<U> supplyAsyncThenApply(Supplier<T> supplier, Function<T, U> mapper) {
        return CompletableFuture
                .supplyAsync(supplier, asyncExecutor)
                .thenApplyAsync(mapper, asyncExecutor); // 确保后续操作也在同一线程池
    }

    /**
     * 批量执行异步任务
     *
     * @param tasks 任务列表
     * @return CompletableFuture<Void>
     */
    @SafeVarargs
    public final CompletableFuture<Void> runAllAsync(Runnable... tasks) {
        CompletableFuture<Void>[] futures = new CompletableFuture[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            futures[i] = CompletableFuture.runAsync(tasks[i], asyncExecutor);
        }
        return CompletableFuture.allOf(futures);
    }

    /**
     * 批量执行有返回值的异步任务
     *
     * @param suppliers 供应商列表
     * @return CompletableFuture包含所有结果的列表
     */
    @SafeVarargs
    public final <T> CompletableFuture<T[]> supplyAllAsync(Supplier<T>... suppliers) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] futures = new CompletableFuture[suppliers.length];
        for (int i = 0; i < suppliers.length; i++) {
            futures[i] = CompletableFuture.supplyAsync(suppliers[i], asyncExecutor);
        }

        return CompletableFuture.allOf(futures)
                .thenApply(v -> {
                    @SuppressWarnings("unchecked")
                    T[] results = (T[]) new Object[futures.length];
                    for (int i = 0; i < futures.length; i++) {
                        results[i] = futures[i].join();
                    }
                    return results;
                });
    }

    /**
     * 带超时的异步任务执行
     *
     * @param supplier      结果供应商
     * @param timeoutMillis 超时时间（毫秒）
     * @param defaultValue  超时时的默认值
     * @return CompletableFuture<T>
     */
    public <T> CompletableFuture<T> supplyAsyncWithTimeout(Supplier<T> supplier, long timeoutMillis, T defaultValue) {
        return CompletableFuture
                .supplyAsync(supplier, asyncExecutor)
                .completeOnTimeout(defaultValue, timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * 异步执行任务，出现异常时返回默认值
     *
     * @param supplier     结果供应商
     * @param defaultValue 异常时的默认值
     * @return CompletableFuture<T>
     */
    public <T> CompletableFuture<T> supplyAsyncWithFallback(Supplier<T> supplier, T defaultValue) {
        return CompletableFuture
                .supplyAsync(supplier, asyncExecutor)
                .exceptionally(throwable -> {
                    // 这里可以记录异常日志
                    System.err.println("异步任务执行异常: " + throwable.getMessage());
                    return defaultValue;
                });
    }

    /**
     * 异步执行任务，出现异常时执行备用逻辑
     *
     * @param supplier         主要任务
     * @param fallbackSupplier 备用任务
     * @return CompletableFuture<T>
     */
    public <T> CompletableFuture<T> supplyAsyncWithFallback(Supplier<T> supplier, Supplier<T> fallbackSupplier) {
        return CompletableFuture
                .supplyAsync(supplier, asyncExecutor)
                .exceptionallyAsync(throwable -> {
                    System.err.println("主要任务失败，执行备用任务: " + throwable.getMessage());
                    return fallbackSupplier.get();
                }, asyncExecutor);
    }
}
