package ThreadPool.JdkThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author jasonR
 * @date 2021/1/26 18:18
 *
 *
 * 这里利用任务调度线程池来实现一个小的功能
 * 让每周四18：00执行定时执行任务
 */
@Slf4j(topic = "TestScheduledThreadPool")
public class TestScheduledThreadPool {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        //当前时间
        LocalDateTime now =LocalDateTime.now();
        //获取周四时间
        LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);

        //如果当前时间大于本周周四，那么必须找到下周周四
        if(now.compareTo(time)>0){
           time=time.plusWeeks(1);
        }

        //initailDelay代表当前时间和周四的时间差
        //period 代表时间间隔
        long initailDelay= Duration.between(now,time).toMillis();

        long period=1000 *60*60*24*7;

        pool.scheduleAtFixedRate(()->{
            log.debug("running...");
        },initailDelay,period, TimeUnit.MILLISECONDS);

    }
}
