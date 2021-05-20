package ConnectionPoll;

import java.sql.Connection;

/**
 * @author jasonR
 * @date 2021/1/11 17:24
 */
public class Testpoll {
    public static void main(String[] args) {
        ConnectionPool pool=new ConnectionPool(2);
        //5个线程竞争两个连接
        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                Connection conn= pool.borrow();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pool.free(conn);
            }).start();
        }
    }
}
