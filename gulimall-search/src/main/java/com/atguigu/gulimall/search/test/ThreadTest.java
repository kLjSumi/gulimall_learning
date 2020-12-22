package com.atguigu.gulimall.search.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kLjSumi
 * @Date 2020/12/22
 */
public class ThreadTest {
    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程： " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果： " + i);
//        }, executor);
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程： " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果： " + i);
            return i;
        }, executor).handle((res,thr) -> {
            if (res != null) {
                return res * 2;
            }
            if (thr != null) {
                return 0;
            }
            return 0;
        });
//        future.whenComplete((res,e)->{
//            //虽然能得到异常信息，没法更改结果， 类似于一个监听器
//            System.out.println("异步任务完成...结果是： " + res + "; 异常： " + e);
//        }).exceptionally(throwable -> 10); //exceptionally感知异常，同时返回默认值
        Integer integer = future.get();
        System.out.println("main...end..."+integer);

    }
}
