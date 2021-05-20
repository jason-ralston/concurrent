package com;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jasonR
 * @date 2020/12/4 16:43
 */
@Slf4j(topic = "Question1")
public class Question1 {
    public static void makeTea(){
        long beginTime=System.currentTimeMillis();
        Thread worker1 = new Thread(() -> {
            log.debug("工人1开始工作");
            log.debug("开始洗水壶");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("洗完水壶了");
            log.debug("开始烧开水");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("烧完开水了");
        }, "worker1");
        Thread worker2 = new Thread(() -> {
            log.debug("工人2开始工作");
            log.debug("开始洗茶壶、洗茶杯、拿茶叶");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("三件活都搞完了");
            try {
                worker1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//等待第一个工人干完活
            log.debug("泡茶");

        }, "worker2");
        worker1.start();
        worker2.start();
        try {
            worker2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }//等工人干完活再计算时间
        long endTime=System.currentTimeMillis();
        log.debug("总用时为：{}ms",endTime-beginTime);

    }

    public static void main(String[] args) {
        makeTea();
    }

}
