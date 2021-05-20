package ThreadPool.JdkThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jasonR
 * @date 2021/1/24 16:02
 */
@Slf4j(topic = "TestThreadPoolExecutors")
public class TestThreadPoolExecutors {
    public void testSubmit() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> future = pool.submit(() -> {
            log.debug("running");
            Thread.sleep(1000);
            return "ok";
        });
        log.debug("{}",future.get());
    }
    public static void testInvokeAll() throws InterruptedException{
        ExecutorService pool = Executors.newFixedThreadPool(2);

        List<Future<String>> futures = pool.invokeAll(Arrays.asList(() -> {
                    log.debug("begin");
                    Thread.sleep(1000);
                    return "1";
                }, () -> {
                    log.debug("begin");
                    Thread.sleep(2000);
                    return "2";
                }
        ));
        futures.forEach(f->{
            try {
                log.debug("{}",f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });


    }
    public static void main(String[] args) {
//        ExecutorService pool= Executors.newFixedThreadPool(2, new ThreadFactory() {
//            private AtomicInteger t=new AtomicInteger(1);
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r,"myPool_t"+t.getAndIncrement());
//            }
//        });
//        pool.execute(()->{
//            log.debug("1");
//        });
//        pool.execute(()->log.debug("2"));
//        pool.execute(()->log.debug("3"));
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);


    }
}
