package com.demo.test;

import com.demo.vo.VO;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import org.slf4j.MDC;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class Test extends Thread{

    private int i;

    public Test() {
    }

    public Test(int i) {
        this.i = i;
    }

    @Override
    public void run() {
        System.out.println(i);
        MDC.put("username",i+"");
        for (int i=0;i<=50;i++){
            System.out.println("aaa"+i);
            if (i == 20){
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("run"+i+"---"+MDC.get("username"));
    }

    public static void main(String[] args) throws Exception {
//        Test test1 = new Test(1);
//        test1.start();
//        Test test2 = new Test(2);
//        test2.start();

        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseClass(VO.class).toPrintable());
        System.out.println("=================");
        Unsafe unsafeInsatance = getUnsafeInsatance();

        VO vo = new VO();
        vo.a=1;
        vo.b=2;
        vo.d=new HashMap<>();
        //通过反射中getDeclaredFile方法仅能获取类本身的属性成员（包括保护,共有，私有）
        //getFile仅能获取类（及其父类）public属性成员
        long a = unsafeInsatance.objectFieldOffset(VO.class.getDeclaredField("a"));
        System.out.println("aoffset"+a);
        int anInt = unsafeInsatance.getInt(vo, a);
        System.out.println("va="+anInt);
    }

    private static Unsafe getUnsafeInsatance() throws Exception {
        //通过反射获取rt.jar下的Unsafe类
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        //确保可以使用私有方法
        theUnsafe.setAccessible(true);
        return (Unsafe) theUnsafe.get(Unsafe.class);
    }
}
