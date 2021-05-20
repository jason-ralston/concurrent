package Atomic;

import sun.misc.Unsafe;



/**
 * @author jasonR
 * @date 2021/1/8 20:05
 */
public class MyAtomicInteger {
    private volatile int value;
    private static  long valueOffset = 0;
    private static Unsafe unsafe;

    static {
        unsafe=UnsafeAccesser.getUnsafe();
        try {
            valueOffset=unsafe.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
    public  int getValue(){
        return value;
    }

    public void compareAndSet(int change){
        unsafe.compareAndSwapInt(this,valueOffset,value,change);

    }



}
