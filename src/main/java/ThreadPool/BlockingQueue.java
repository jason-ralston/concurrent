package ThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jasonR
 * @date 2021/1/11 20:21
 * 自定义线程池的阻塞队列
 */
@Slf4j(topic = "BlockingQueue")
public class BlockingQueue<T> {
    //1.任务队列
    private Deque<T> queue=new ArrayDeque<>();
    //arrayDeque的性能比LinkedList好一些

    //2.锁
    private ReentrantLock lock=new ReentrantLock();

    //3.生产者条件变量
    private Condition fullWaitSet=lock.newCondition();

    //4.消费者条件变量
    private Condition emptyWaitSet=lock.newCondition();

    //5.容量
    private int capcity;

    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    //带超时的阻塞获取
    public T poll(long timeout, TimeUnit unit){
        lock.lock();
        try{
            //将超时时间统一转换为纳秒
            long nanos=unit.toNanos(timeout);
            while(queue.isEmpty()){
                try{
                    //返回的是剩余时间
                    if(nanos<=0){
                        return null;
                    }
                    nanos=emptyWaitSet.awaitNanos(nanos);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }


    //阻塞获取
    public T take(){
        lock.lock();
        try{
            while(queue.isEmpty()){
                try{
                    emptyWaitSet.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }

    //阻塞添加
    public void put(T element){
        lock.lock();
        try{
            while(queue.size()==capcity){
                try{
                    log.debug("等待加入任务队列{}。。。",element);
                    fullWaitSet.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列{}",element);
            queue.addLast(element);
            emptyWaitSet.signal();
        }finally {
            lock.unlock();
        }
    }

    //带超时时间的阻塞添加
    public boolean offer(T task,long timeout,TimeUnit timeUnit){
        lock.lock();
        try{
            long nanos=timeUnit.toNanos(timeout);
            while(queue.size()==capcity){
                try{
                    log.debug("等待加入任务队列。。",task);
                    if(nanos<=0){
                        return false;
                    }
                    nanos=fullWaitSet.awaitNanos(nanos);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}",task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        }finally {
            lock.unlock();
        }
    }



    //获取大小
    public int size(){
        lock.lock();
        try{
            return queue.size();
        }finally {
            lock.unlock();
        }
    }

    public void tryPut(ThreadPool.RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try{
            if(queue.size()==capcity){
                rejectPolicy.reject(this,task);
            }else{
                log.debug("加入任务队列 {}",task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        }finally {
            lock.unlock();
        }
    }
}
