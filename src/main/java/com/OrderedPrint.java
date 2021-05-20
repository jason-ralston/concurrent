package com;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jasonR
 * @date 2020/12/19 17:02
 * 线程同步模式的交替输出
 *让三个线程交替输出abc abc abc abc abc
 */
public class OrderedPrint {
    static Thread t1;
    static Thread t2;
    static Thread t3;
    //park unpark用的
    public static void main(String[] args) {
//        //waitNotify
//        WaitNotify wn=new WaitNotify(1,3);
//        new Thread(()->{
//            wn.print("a",1,2);
//        }).start();
//        new Thread(()->{
//            wn.print("b",2,3);
//        }).start();
//        new Thread(()->{
//            wn.print("c",3,1);
//        }).start();
//        //reentrantLock
//        AwaitSignal awaitSignal=new AwaitSignal(3);
//        Condition a=awaitSignal.newCondition();
//        Condition b=awaitSignal.newCondition();
//        Condition c=awaitSignal.newCondition();
//        new Thread(()->
//            awaitSignal.print("a",a,b)
//        ).start();
//        new Thread(()->
//                awaitSignal.print("b",b,c)
//        ).start();
//        new Thread(()->
//                awaitSignal.print("c",c,a)
//        ).start();
//        //因为它们获得锁之后会先进入wait，因此得先把a唤醒
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        awaitSignal.lock();
//        try{
//            System.out.println("开始。。。");
//            a.signal();
//        }finally {
//            awaitSignal.unlock();
//        }
//    //有一个问题，没有使用while方式防止虚假唤醒，但是由于这个例子中每一个条件变量中只有一个线程,因此没事
        ParkUnpark pu=new ParkUnpark(3);

        t1= new Thread(
                ()->pu.print("a",t2)
        ,"t1");
        t2= new Thread(
                ()->pu.print("b",t3)
                ,"t1");
        t3= new Thread(
                ()->pu.print("c",t1)
                ,"t1");
        t1.start();
        t2.start();
        t3.start();
        LockSupport.unpark(t1);





    }


}
class WaitNotify{
    //等待标记
    private int flag;

    //每个线程做完自己的工作之后，将标记置为下任务应该的标记

    private int loopNumber;
    //循环次数


    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }
    public void print(String str,int waitFlag,int nextFlag){
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this){
                while(flag!=waitFlag){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                flag=nextFlag;
                this.notifyAll();
            }
        }
    }
}

class AwaitSignal extends ReentrantLock {
    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String str, Condition current, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            lock();
            try {
                try {
                    current.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(str);
                next.signal();
            } finally {
                unlock();
            }
        }
    }
}

class ParkUnpark{
    private  int loopNumber;
    public void print(String str,Thread next){
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            System.out.print(str);
            LockSupport.unpark(next);
        }
    }

    public ParkUnpark(int loopNumber) {
        this.loopNumber = loopNumber;
    }
}
