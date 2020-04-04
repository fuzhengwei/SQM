package org.itstack.sqm.asm.probe;

import org.itstack.sqm.base.MethodTag;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfilingMethodVisitor extends AdviceAdapter {


    private int startTimeIdentifier;
    private int parameterIdentifier;
    private List<String> parameterTypeList = new ArrayList<>();
    private int cursor = 0;
    private int localCount = 0;
    private int methodId = -1;

    protected ProfilingMethodVisitor(int access, String methodName, String desc, MethodVisitor mv, String className, String fullClassName, String simpleClassName) {
        super(ASM5, mv, access, methodName, desc);

        //(String var1,Object var2,String var3,int var4,long var5,int[] var6,Object[][] var7,Req var8)=="(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;IJ[I[[Ljava/lang/Object;Lorg/itstack/test/Req;)V"
        Matcher matcher = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})").matcher(desc.substring(0, desc.lastIndexOf(')') + 1));
        while (matcher.find()) {
            parameterTypeList.add(matcher.group(1));
        }

        methodId = ProfilingAspect.generateMethodId(new MethodTag(fullClassName, simpleClassName, methodName, desc, parameterTypeList, desc.substring(desc.lastIndexOf(')') + 1)));
    }

    @Override
    protected void onMethodEnter() {
        // 1.方法执行时启动纳秒
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        startTimeIdentifier = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(LSTORE, startTimeIdentifier);

        // 2. 方法入参
        int parameterCount = parameterTypeList.size();
        // 初始化数组长度
        if (parameterCount > 0) {
            if (parameterCount >= 4) {
                mv.visitVarInsn(BIPUSH, parameterCount);//初始化数组长度
            } else {
                switch (parameterCount) {
                    case 1:
                        mv.visitInsn(ICONST_1);
                        break;
                    case 2:
                        mv.visitInsn(ICONST_2);
                        break;
                    case 3:
                        mv.visitInsn(ICONST_3);
                        break;
                    default:
                        mv.visitInsn(ICONST_0);
                }
            }
            mv.visitTypeInsn(ANEWARRAY, Type.getDescriptor(Object.class));

            // 给数组赋参数值
            for (int i = 0; i < parameterCount; i++) {
                mv.visitInsn(DUP);
                if (i > 3) {
                    mv.visitVarInsn(BIPUSH, i);
                } else {
                    switch (i) {
                        case 0:
                            mv.visitInsn(ICONST_0);
                            break;
                        case 1:
                            mv.visitInsn(ICONST_1);
                            break;
                        case 2:
                            mv.visitInsn(ICONST_2);
                            break;
                        case 3:
                            mv.visitInsn(ICONST_3);
                            break;
                    }
                }


                String type = parameterTypeList.get(i);
                if ("Z".equals(type)) {
                    mv.visitVarInsn(ILOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                } else if ("C".equals(type)) {
                    mv.visitVarInsn(ILOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                } else if ("B".equals(type)) {
                    mv.visitVarInsn(ILOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                } else if ("S".equals(type)) {
                    mv.visitVarInsn(ILOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                } else if ("I".equals(type)) {
                    mv.visitVarInsn(ILOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                } else if ("F".equals(type)) {
                    mv.visitVarInsn(FLOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                } else if ("J".equals(type)) {
                    mv.visitVarInsn(LLOAD, ++cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                } else if ("D".equals(type)) {
                    cursor += 2;
                    mv.visitVarInsn(DLOAD, cursor);  //获取对应的参数
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                } else {
                    ++cursor;
                    mv.visitVarInsn(ALOAD, cursor);  //获取对应的参数
                }
                mv.visitInsn(AASTORE);
            }

            // 5
            parameterIdentifier = startTimeIdentifier + cursor + 1;

            mv.visitVarInsn(ASTORE, parameterIdentifier);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW) {

            switch (opcode) {
                case RETURN:
                    break;
                case ARETURN:
                    mv.visitVarInsn(ASTORE, ++localCount); // 6
                    mv.visitVarInsn(ALOAD, localCount);    // 6
                    break;
            }

            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodId);
            if (parameterTypeList.isEmpty()) {
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(ProfilingAspect.class), "point", "(JI)V", false);
            } else {
                mv.visitVarInsn(ALOAD, parameterIdentifier);  // 5
                mv.visitVarInsn(ALOAD, localCount);           // 6
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(ProfilingAspect.class), "point", "(JI[Ljava/lang/Object;Ljava/lang/Object;)V", false);
            }

        }
    }

}
