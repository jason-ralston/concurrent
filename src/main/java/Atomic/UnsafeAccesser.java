package Atomic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jasonR
 * @date 2021/1/8 20:11
 */
public class UnsafeAccesser {
    private static  Unsafe unsafe = null;
    static {
        try{
        Field theUnsafe= Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe=(Unsafe) theUnsafe.get(null);

        } catch (IllegalAccessException a){
        a.printStackTrace();
        }catch (NoSuchFieldException e) {
        e.printStackTrace();
        }
    }
    public static Unsafe getUnsafe(){return unsafe;}
}


