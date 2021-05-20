package AQS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author jasonR
 * @date 2021/2/15 15:59
 */

//自定义不可重入锁
public class MyLock implements Lock {

    private Mysync sync= new Mysync();

    @Override //加锁，不成功进入等待队列
    public void lock() {
        sync.acquire(1);
    }

    @Override //加锁，是可打断的
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override //尝试加锁（一次）
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override //尝试加锁，带超时时间
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override //解锁
    public void unlock() {
        sync.release(0);
    }

    @Override //条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }

    private  class Mysync  extends  AbstractQueuedSynchronizer{
        @Override //尝试加锁
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0,1)){
                //加锁成功
                setExclusiveOwnerThread(Thread.currentThread());
                //owner设置为当前线程
                return true;
            }
            return false;
        }

        @Override //尝试解锁
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);
            //state 使用了volatile修饰，会加写屏障，同步前面的修改
            return true;

        }

        @Override //判断是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState()==1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }
}
