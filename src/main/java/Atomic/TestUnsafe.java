package Atomic;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jasonR
 * @date 2021/1/8 19:53
 */
public class TestUnsafe {
    public static void main(String[] args) {
        try {
            Field theUnsafe= Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe=(Unsafe) theUnsafe.get(null);
            
            
            //1.获取域的偏移地址
            long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
            long nameOffset=unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));
            Teacher teacher=new Teacher();

            //2.执行cas操作
            unsafe.compareAndSwapInt(teacher,idOffset,0,1);
            unsafe.compareAndSwapObject(teacher,nameOffset,null,"张三");
            System.out.println(teacher);

        } catch (IllegalAccessException a){
            a.printStackTrace();
        }catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}


@Data
class Teacher{
    volatile  int id;
    volatile String name;
}