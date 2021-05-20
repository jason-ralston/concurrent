package ThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * @author jasonR
 * @date 2021/1/11 20:52
 */
@Slf4j(topic = "Test")
public class TestThreadPool {
    public static void main(String[] args) {
        ThreadPool threadPool=new ThreadPool(2,1000, TimeUnit.MICROSECONDS,10,(queue,task)->{
            //1.死等
//            queue.offer(task,500,TimeUnit.MILLISECONDS);
            //2.超时等待
            queue.offer(task,500, TimeUnit.MILLISECONDS);
        });
        for (int i = 0; i < 13; i++) {
            int j=i;
            threadPool.execute(()->{
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
