package ThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @author jasonR
 * @date 2021/1/11 20:36
 * 线程池的实现类
 */
@Slf4j(topic = "ThreadPool")
public class ThreadPool {
    //任务队列
    private  BlockingQueue<Runnable> taskQueue;
    //线程集合

    private HashSet<Worker> workers=new HashSet<>();

    //核心线程数
    private  int coreSize;

    //获取任务超时时间
    private long timeout;

    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;

    //执行任务
    public void execute(Runnable task){
        //当任务数没有超过核心数时，直接交给worker对象执行
        //当超过时，加入任务队列暂存
        synchronized (workers) {
            if(workers.size()<coreSize){
                Worker worker=new Worker(task);
                log.debug("新增worker{},{}",worker,task);
                workers.add(worker);
                worker.start();
            }else{
//                taskQueue.put(task);
                //1）死等
                //2）超时等待
                //3）放弃任务执行
                //4）抛出异常
                //5）让调用者自己执行任务

                //把决策权给调用者，使用策略模式
                taskQueue.tryPut(rejectPolicy,task);
            }
        }
    }


    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit,int queueCapcity,RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue=new BlockingQueue<Runnable>(queueCapcity);
        this.rejectPolicy=rejectPolicy;
    }

    class Worker extends Thread{
        private Runnable task;
        public Worker(Runnable task){
            this.task=task;
        }

        @Override
        public void run() {
            //执行任务
            //1)当task不为空，执行任务
            //2）当task执行完毕，再从任务队列获取任务执行
            while(task!=null || (task=taskQueue.poll(timeout,timeUnit))!=null){
                try {
                    log.debug("正在执行。。。{}",task);
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }finally{
                    task=null;
                }
            }
            synchronized (workers){
                log.debug("线程被移除。。。{}",this);
                workers.remove(this);
            }
        }
    }
@FunctionalInterface//拒绝策略
interface  RejectPolicy <T>{
        void reject(BlockingQueue<T> queue,T task);
}

}
