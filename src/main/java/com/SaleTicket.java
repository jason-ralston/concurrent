package com;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * @author jasonR
 * @date 2020/12/8 15:03
 */
@Slf4j(topic = "saleTicket")
public class SaleTicket {
    static Random random =new Random();
    //random是线程安全的
    public static int randomAmount() {
        return random.nextInt(5) + 1;

    }
    public static void main(String[] args) {
        TicketWindow window=new TicketWindow(1000);
        //把所有线程放到一个集合里，然后使用for循环依次调用join函数
        List<Thread> list =new ArrayList<>();
        //存储卖出去多少张票
        List<Integer> sellCount=new Vector<>();//vector是线程安全的
        for(int i =0;i<2000;i++){
            Thread t=new Thread(()->{
               //临界区
               int count=window.sell(randomAmount());
               sellCount.add(count);
            });
            list.add(t);
            t.start();
        }
        list.forEach((t)->{
            try{
                t.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        //卖出去的票求和
        log.debug("sold count:{}",sellCount.stream().mapToInt(c->c).sum());
        //剩余票数
        log.debug("remainder count:{}",window.getCount());

    }
}
class TicketWindow{
    private int count;
    public TicketWindow(int count){
        this.count=count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public synchronized int sell(int amount){
        if(this.count>=amount){
            this.count-=amount;
            return amount;
        }else {
            return 0;
        }

    }
}