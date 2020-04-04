package org.itstack.sqm.asm.probe;

import com.alibaba.fastjson.JSON;
import org.itstack.sqm.base.MethodTag;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ProfilingAspect {

    public static final int MAX_NUM = 1024 * 32;

    private final static AtomicInteger index = new AtomicInteger(0);
    private final static AtomicReferenceArray<MethodTag> methodTagArr = new AtomicReferenceArray<>(MAX_NUM);

    public static int generateMethodId(MethodTag tag) {
        int methodId = index.getAndIncrement();
        if (methodId > MAX_NUM) return -1;
        methodTagArr.set(methodId, tag);
        return methodId;
    }

    public static void point(final long startNanos, final int methodId) {
        MethodTag method = methodTagArr.get(methodId);
        System.out.println("类名：" + method.getFullClassName());
        System.out.println("方法：" + method.getMethodName());
        System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
    }

    public static void point(final long startNanos, final int methodId, Object[] requests) {
        MethodTag method = methodTagArr.get(methodId);
        System.out.println("类名：" + method.getFullClassName());
        System.out.println("方法：" + method.getMethodName());
        System.out.println("参数[类型]：" + JSON.toJSONString(method.getParameterTypeList()));
        System.out.println("参数[值]：" + JSON.toJSONString(requests));
        System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
    }

    public static void point(final long startNanos, final int methodId, Object[] requests, Object response) {
        MethodTag method = methodTagArr.get(methodId);
        System.out.println("监控 - Begin");
        System.out.println("类名：" + method.getFullClassName());
        System.out.println("方法：" + method.getMethodName());
        System.out.println("入参类型：" + JSON.toJSONString(method.getParameterTypeList()));
        System.out.println("入数[值]：" + JSON.toJSONString(requests));
        System.out.println("出参类型：" + method.getReturnParameterType());
        System.out.println("出参[值]：" + JSON.toJSONString(response));
        System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
        System.out.println("监控 - End\r\n");
    }

}
