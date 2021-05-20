package com;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jasonR
 * @date 2020/12/19 20:57
 */
@Slf4j(topic = "TestVolatile")
public class TestVolatile {
    static boolean b=true;
    public static void main(String[] args) {
        new Thread(()->{
            while(b){

            }
        },"t1").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        b=false;

    }
}
