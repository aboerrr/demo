package com.demo;

import com.demo.vo.User;
import net.minidev.json.JSONUtil;
import org.apache.logging.log4j.spi.CopyOnWrite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void task1(){
        String a = "q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,k,j,l,z,x,c,v,b,n,m,1;2;3;4;5;6;7;8;9;Q/W/E/R/T/Y/U/I/O/PA/S/D/F/G/H/J/K/L/Z/X/C/V/B/N/M";
        int i = a.lastIndexOf(",");
        String y = a.substring(0,a.lastIndexOf(","));
        String h = a.substring(a.lastIndexOf(",")+1,a.lastIndexOf(";"));
        String b = a.substring(a.lastIndexOf(";")+1,a.lastIndexOf("/"));
//        ArrayList<Object> objects = null;
//        for (String s : y) {
//            objects = new ArrayList<>();
//            objects.add(s);
//        }
//        System.out.println(objects);
//
        List<String> strings;
        //字符串直接切割后遍历输出
        Arrays.asList(y.split(",")).stream().sorted().forEach(System.out::print);
        Arrays.asList(h.split(";")).stream().sorted().forEach(System.out::print);
        Arrays.asList(b.split("/")).stream().sorted().forEach(System.out::print);

        String[] y1 = y.split(",");
        String[] h1 = y.split(";");
        String[] b1 = y.split("/");
        System.out.println(y);
        System.out.println(h);
        System.out.println(b);
        System.out.println(y1);
        System.out.println(h1);
        System.out.println(b1);
    }

    @Test
    public void test2() {
        /*
        要求用一行代码实现
        现在有6个用户
        筛选：
        ID 必须是偶数
        年龄必须大于3岁
        用户名转为大写字母
        用户名字母倒着排序
        只输出一个用户的名字
         */
        User user1 = new User(1,"a",1);
        User user2 = new User(2,"b",2);
        User user3 = new User(3,"c",3);
        User user4 = new User(4,"d",4);
        User user5 = new User(5,"e",5);
        User user6 = new User(6,"f",6);

        List<User> users = Arrays.asList(user1, user2, user3, user4, user5, user6);
        users.stream()
                .filter(a->a.getId()%2==0)
                .filter(a->a.getAge()>3)
                .map(a->a.getName().toUpperCase())
                .sorted((a1,a2)->{return a2.compareTo(a1);})
                .limit(1)
                .forEach(System.out::print);
    }
    @Test
    public void test3(){

        String a = "qweASD";
        //据说是有可以将字符串直接转成stream流的方法，但是百度没找到
        String[] as = {"1,2,3,4,5,6"};
        List<String> strings = Arrays.asList(a);
        strings.stream()
                .map(b->b.toLowerCase())
                .sorted((b1,b2)->b2.compareTo(b1))
                .forEach(System.out::print);
    }

    @Test
    public void test4(){

        Stream.of("18:小明:18","19:小美:19","20:小强:20").map(a->{
            String[] split = a.split(":");
            return new User((Integer.valueOf( split[0])),split[1],(Integer.valueOf( split[2])));})
                .forEach(System.out::println);

//        String a = "qweASD";
//        //据说是有可以将字符串直接转成stream流的方法，但是百度没找到
//        String[] split = a.split("");
//        Stream<String> stream = Arrays.stream(split);
//        stream.forEach(System.out::println);
    }

}
