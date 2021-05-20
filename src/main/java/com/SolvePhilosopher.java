package com;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jasonR
 * @date 2020/12/18 17:41
 *
 * 哲学家就餐（解决死锁）
 */
@Slf4j(topic = "SolvePhilosopher")
public class SolvePhilosopher extends Thread {
    LockChopstick left;
    LockChopstick right;
    public SolvePhilosopher(String name,LockChopstick left,LockChopstick right){
        super(name);
        this.left=left;
        this.right=right;
    }

    @Override
    public void run() {
        while(true){
            //左手筷子
            try {
                if(left.tryLock(1,TimeUnit.SECONDS)){
                    try{
                        //右手筷子
                        if(right.tryLock(1, TimeUnit.SECONDS)){
                            try{
                                eat();
                            }finally {
                                right.unlock();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        left.unlock();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void eat() {
        log.debug("use{},{} eating...",left.name,right.name);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LockChopstick c1=new LockChopstick("1");
        LockChopstick c2=new LockChopstick("2");
        LockChopstick c3=new LockChopstick("3");
        LockChopstick c4=new LockChopstick("4");
        LockChopstick c5=new LockChopstick("5");
        new SolvePhilosopher("苏格拉底",c1,c2).start();
        new SolvePhilosopher("柏拉图",c2,c3).start();
        new SolvePhilosopher("亚里士多德",c3,c4).start();
        new SolvePhilosopher("赫拉克利特",c4,c5).start();
        new SolvePhilosopher("阿基米德",c5,c1).start();
        //巴赫、亨德尔
    }

}
class LockChopstick extends ReentrantLock {
    String name;

    public LockChopstick(String name) {
        this.name = name;
    }
}

