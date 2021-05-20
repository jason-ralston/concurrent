package com;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jasonR
 * @date 2020/12/23 14:04
 * 测试cas
 */
public interface Account {
    //获取余额
    Integer getBalance();

    //取款
    void withdraw(Integer amount);

    /**
     * 方法内会启动1000个线程，每个线程做-10元的操作
     * 如果初始余额为10000，则正确结果应该是0
     *
     * */
    static void demo(Account account){
        List<Thread> ts=new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(()->account.withdraw(10)));
        }
        long start=System.nanoTime();
        ts.forEach(Thread::start);
        ts.forEach(t->{
            try{
                t.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        long end=System.nanoTime();
        System.out.println(account.getBalance()+" cost: "+(end-start)/1000_000+" ms");


    }
}
class AccountCas implements Account{
    private AtomicInteger balance;

    public AccountCas(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        while(true){

            int prev = balance.get();
            int next=prev-amount;
            if(balance.compareAndSet(prev,next)){
                break;
            }

        }
    }

    public static void main(String[] args) {
        Account account=new AccountCas(10000);
        Account.demo(account);

    }
}