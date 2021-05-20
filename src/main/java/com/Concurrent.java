package com;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


/**
 * @author jasonR
 * @date 2020/12/2 8:55
 */
@Slf4j(topic = "Concurrent")
public class Concurrent {
    public static void futureTask() throws InterruptedException, ExecutionException {
        FutureTask<Integer> task=new FutureTask<>(()->
        {log.debug("futureTask running");
        return 100;});
        new Thread(task).start();
        Integer result=task.get();
        log.debug("result is:{}",result);

    }
    public static void testRun(){
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("enter sleep");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("has been interrupted");//被唤醒了，所以其实这个异常是为了让线程继续自己的工作
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        try {
            Thread.sleep(1000);//等一段时间，等就绪的线程运行起来
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("interrupt");
        t1.interrupt();//打断一下
    }
    public static void testInterrupt() throws InterruptedException{
        Thread t1 = new Thread(() -> {
            log.debug("我运行了");
            try {
                log.debug("我休眠了");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.debug("我又活了");
                log.debug("我运行完了");
            }
        },"t1") {
        };
        t1.start();
        Thread.sleep(1000);
        log.debug("打断标记：{}",t1.isInterrupted());
        t1.interrupt();
        Thread.sleep(200);
        log.debug("打断标记：{}",t1.isInterrupted());
    }
    public static void testDaemon() throws InterruptedException{
        Thread daemon = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.debug("守护线程结束");
                    break;
                }
            }

        });
        daemon.setDaemon(true);//设为守护线程
        daemon.start();
        Thread.sleep(1000);
        log.debug("主线程结束");

    }
    public static void testAdmire() throws InterruptedException{
        Room room=new Room();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.increment();
            }
        },"t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.decrement();
            }
        },"t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("admireCount= {}",room.getCount());
    }

    public static void main(String[] args) throws InterruptedException {
        testAdmire();

    }
}

class Room{
    private int count;
    public void increment(){
        synchronized (this){
            count++;
        }
    }
    public void decrement(){
        synchronized (this){
            count--;
        }
    }
    public int getCount(){

        synchronized (this){
            return count;
        }
    }



}