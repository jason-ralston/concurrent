package com;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author jasonR
 * @date 2020/12/8 19:53
 */
@Slf4j(topic = "Biased")
public class TestBiased {

    public static void main(String[] args) {
        Dog dog = new Dog();
        new Thread(()->{
            //加锁前
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
            synchronized (dog){
                log.debug(ClassLayout.parseInstance(dog).toPrintable());
            }
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
            synchronized (TestBiased.class){
                TestBiased.class.notify();
            }
        },"t1").start();
        new Thread(()->{
            //线程2先等一下，错开运行时间
            synchronized (TestBiased.class){
                try {
                    TestBiased.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //加锁前
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
            synchronized (dog){
                log.debug(ClassLayout.parseInstance(dog).toPrintable());
            }
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        },"t2").start();
        //需要将两个线程访问的时间错开，如果不错开，就会升级为重量级锁
    }
}
class Dog{

}
