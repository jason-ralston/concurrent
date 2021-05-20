package com;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author jasonR
 * @date 2020/12/14 21:40
 * 利用消息队列的生产者消费者
 */
//消息队列类，java线程之间通信
@Slf4j(topic = "MessageQueue")
public class MessageQueue {
    //队列集合
    private LinkedList<Message> list=new LinkedList<>();
    //容量
    private int capcity;

    public MessageQueue(int capcity){
        this.capcity=capcity;
    }
    //获取消息
    public Message take(){
        //检查队列是否为空
        synchronized (list){
        while(list.isEmpty()){

            try {
                log.debug("队列为空，消费者线程等待");
                list.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
            Message message=list.removeFirst();
            log.debug("已经消费消息{}",message.toString());
            list.notifyAll();
            return message ;
        }
    }
    //存入消息
    public  void put(Message message){
        synchronized (list){
            while(list.size()==capcity){
                try {
                    log.debug("队列满了，生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //加在尾部

            }
            list.addLast(message);
            log.debug("已经生产消息{}",message);
            list.notifyAll();
        }
    }

    public static void main(String[] args) {
        MessageQueue queue=new MessageQueue(2);

        for (int i=0;i<10;i++) {
            int id=i;
            new Thread(()->{
                queue.put(new Message(id,"值"+id));
            },"生产者"+i).start();
            i++;

        }
        new Thread(()->{
            while(true){
                try {
                    Thread.sleep(1000);
                    Message message=queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"消费者").start();

    }

}

//不可变类
final class Message{
    private int id;
    private Object val;

    public Message(int id, Object val) {
        this.id = id;
        this.val = val;
    }

    public int getId() {
        return id;
    }

    public Object getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", val=" + val +
                '}';

    }
}
