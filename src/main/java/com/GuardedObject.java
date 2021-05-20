package com;

import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author jasonR
 * @date 2020/12/14 19:37
 * 设计模式保护性暂停
 */

//中间要传递的消息
public class GuardedObject {
    private int id;
    //唯一标识
    private Object response;
    //结果

    //获取结果
    //timeout 表示等待多久
    public Object get(long timeout){
        synchronized (this){
            //没有结果就一直等待
            //开始时间
            long begin=System.currentTimeMillis();
            //经历时间
            long passTime=0;
            while(response==null) {
                if(passTime>=timeout){
                    break;
                }
                try {
                    this.wait(timeout-passTime);//这里是为了避免虚假唤醒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passTime=System.currentTimeMillis()-begin;
            }
        }

        return response;
    }
    //产生结果
    public void complete(Object response){

        synchronized (this){
            //给结果成员变量赋值
            this.response=response;
            this.notify();
        }
    }

    public GuardedObject(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        Thread.sleep(1000);
        for (Integer id : Mailbox.getIds()) {
            new PostMan(id,"内容"+id).start();
        }

    }
}

class Mailbox{
    private static Map<Integer,GuardedObject> boxes=new Hashtable<>();
    //保证线程安全

    private static int id=1;
    //把id的产生放在box内部
    private static synchronized int generateId(){
        return id++;
    }

    public static GuardedObject getGuardedObject(int key){
        return boxes.remove(key);
        //防止过于臃肿
    }

    public static GuardedObject createGuardedObject(){
        GuardedObject g=new GuardedObject(generateId());
        boxes.put(g.getId(),g);//boxes是线程安全的
        return g;
    }

    public static Set<Integer> getIds(){
        return boxes.keySet();
    }
}

@Slf4j(topic = "people")
class People extends Thread{
    @Override
    public void run() {
       //收信
        GuardedObject guardedObject=Mailbox.createGuardedObject();
        log.debug("收信 id:{}",guardedObject.getId());
        Object mail=guardedObject.get(5000);//等待5s
        log.debug("收到信,id:{},内容:{}",guardedObject.getId(),mail);


    }
}

@Slf4j(topic = "postman")
class PostMan extends Thread{
    private int id;
    private String mail;
    @Override
    public void run() {
        GuardedObject guardedObject=Mailbox.getGuardedObject(id);
        log.debug("送信 id：{},内容：{}",id,mail);
        guardedObject.complete(mail);
    }
    public PostMan(int id,String mail){
        this.id=id;
        this.mail=mail;
    }
}

