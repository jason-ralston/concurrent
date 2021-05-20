package com;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jasonR
 *
 * @date 2020/12/3 22:31
 * 两阶段终止模式的代码实现
 *
 */
@Slf4j(topic = "TwoPhaseTermination")
public class TwoPhaseTermination {
    private Thread monitor;
    public void start(){
        monitor=new Thread(()->{
            while(true){
                Thread current = Thread.currentThread();//获取当前线程
                if (current.isInterrupted()){
                    log.debug("料理后事");
                    break;
                }
                try {
                    Thread.sleep(1000);
                    log.debug("保存监控记录");
                } catch (InterruptedException e) {
                    //重新设置中断标记
                    current.interrupt();
                }
            }
        });
        monitor.start();
    }
    public void stop(){
        monitor.interrupt();
    }
}
class Test{
    //这里可以设置一个用volatile修饰的变量来代替中断标记
    public static void main(String[] args) {
        TwoPhaseTermination t=new TwoPhaseTermination();
        t.start();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.stop();
    }
}