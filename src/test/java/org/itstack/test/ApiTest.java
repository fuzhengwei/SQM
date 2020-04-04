package org.itstack.test;

import org.itstack.sqm.asm.probe.ProfilingAspect;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * -javaagent:E:\itstack\git\github.com\ServerQualityMonitor\target\ServerQualityMonitor-1.0-SNAPSHOT.jar
 */
public class ApiTest extends T {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    private List<String> parameterTypeList = new ArrayList<String>() {{
        add("xxx");
    }};

    public static void main(String[] args) throws InterruptedException {
        ApiTest apiTest = new ApiTest();
        String res01 = apiTest.queryUserInfo(111, 17);
        System.out.println("测试结果：" + res01 + "\r\n");
        String res02 = apiTest.queryUserInfoList("花花", 17, 20190103991L);
        System.out.println("测试结果：" + res02 + "\r\n");
    }

//    public void echoHi() throws InterruptedException {
//        for (int i = 0; i < 10000; i++) {
//            new StringBuffer(i);
//        }
//        logger.info("Hi ServerQualityMonitor!");
//    }

    public String queryUserInfo(int uId, int age) throws InterruptedException {
        return "哈哈哈被你抓到了";
    }

    public String queryUserInfoList(String name, int age, long number) throws InterruptedException {
        Thread.sleep(100);
        return name + ":" + age + ":" + number;
    }

    public void queryUserInfoObj(int uId, int age, int[] req) throws InterruptedException {
        Thread.sleep(100);
        logger.info("查询用户信息，用户Id：{}", uId);
    }

    //
//
//    public void queryUserInfoObj3(Integer uId, Integer age, int[] req) throws InterruptedException {
//        Thread.sleep(100);
//        //        ProfilingAspect.point(1000L, "", "", var2);
//        logger.info("查询用户信息，用户Id：{}", uId);
//    }
//
//    //
//    public void queryUserInfoObj4(boolean a, char b, byte c, short d, int e, float f, long j, double h) {
////        Object[] var2 = new Object[]{a, b, c, d, e, f, j, h};
//    }
//


//    @Test
//    public void test_desc() {
//        String desc = "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;IJ[I[[Ljava/lang/Object;Lorg/itstack/test/Req;)Ljava/lang/String;";
//
//        Pattern p5 = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})");
//        Matcher m5 = p5.matcher(desc.substring(0, desc.lastIndexOf(')') + 1));
//
//        while (m5.find()) {
//            String block = m5.group(1);
//            System.out.println(block);
//        }
//
//    }


}
