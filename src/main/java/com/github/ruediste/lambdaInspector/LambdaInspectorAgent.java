package com.github.ruediste.lambdaInspector;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

@SuppressWarnings("restriction")
public class LambdaInspectorAgent {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new InnerClassLambdaMetafactoryTransformer(), true);
        try {
            inst.retransformClasses(Class.forName("java.lang.invoke.InnerClassLambdaMetafactory"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class InnerClassLambdaMetafactoryTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className.equals("java/lang/invoke/InnerClassLambdaMetafactory")) {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, 0);
                cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                            String[] exceptions) {
                        return new MethodVisitor(Opcodes.ASM5,
                                super.visitMethod(access, name, desc, signature, exceptions)) {
                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc,
                                    boolean itf) {
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                                if ("jdk/internal/org/objectweb/asm/ClassWriter".equals(owner)
                                        && "visit".equals(name)) {
                                    // get cw
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "cw", "Ljdk/internal/org/objectweb/asm/ClassWriter;");

                                    // visitAnnotation()
                                    mv.visitLdcInsn("Lcom/github/ruediste/lambdaInspector/LambdaInformation;");
                                    mv.visitInsn(Opcodes.ICONST_1);
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/ClassWriter", "visitAnnotation",
                                            "(Ljava/lang/String;Z)Ljdk/internal/org/objectweb/asm/AnnotationVisitor;",
                                            false);

                                    // set owner
                                    mv.visitInsn(Opcodes.DUP);
                                    mv.visitLdcInsn("implMethodClass");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "implMethodClassName", "Ljava/lang/String;");
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "jdk/internal/org/objectweb/asm/Type",
                                            "getObjectType",
                                            "(Ljava/lang/String;)Ljdk/internal/org/objectweb/asm/Type;", false);
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/AnnotationVisitor", "visit",
                                            "(Ljava/lang/String;Ljava/lang/Object;)V", false);

                                    // set name
                                    mv.visitInsn(Opcodes.DUP);
                                    mv.visitLdcInsn("implMethodName");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "implMethodName", "Ljava/lang/String;");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/AnnotationVisitor", "visit",
                                            "(Ljava/lang/String;Ljava/lang/Object;)V", false);

                                    // set desc
                                    mv.visitInsn(Opcodes.DUP);
                                    mv.visitLdcInsn("implMethodDesc");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "implMethodDesc", "Ljava/lang/String;");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/AnnotationVisitor", "visit",
                                            "(Ljava/lang/String;Ljava/lang/Object;)V", false);

                                    // set SAM class
                                    mv.visitInsn(Opcodes.DUP);
                                    mv.visitLdcInsn("samClass");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "samBase", "Ljava/lang/Class;");
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "jdk/internal/org/objectweb/asm/Type",
                                            "getType", "(Ljava/lang/Class;)Ljdk/internal/org/objectweb/asm/Type;",
                                            false);
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/AnnotationVisitor", "visit",
                                            "(Ljava/lang/String;Ljava/lang/Object;)V", false);

                                    // set SAM method name
                                    mv.visitInsn(Opcodes.DUP);
                                    mv.visitLdcInsn("samMethodName");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "samMethodName", "Ljava/lang/String;");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/AnnotationVisitor", "visit",
                                            "(Ljava/lang/String;Ljava/lang/Object;)V", false);

                                    // set SAM descriptor
                                    mv.visitInsn(Opcodes.DUP);
                                    mv.visitLdcInsn("samMethodDesc");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/invoke/InnerClassLambdaMetafactory",
                                            "samMethodType", "Ljava/lang/invoke/MethodType;");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodType",
                                            "toMethodDescriptorString", "()Ljava/lang/String;", false);
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                            "jdk/internal/org/objectweb/asm/AnnotationVisitor", "visit",
                                            "(Ljava/lang/String;Ljava/lang/Object;)V", false);

                                }
                            }
                        };
                    }
                }, 0);
                return cw.toByteArray();
            }
            return null;
        }
    }

    ClassWriter cw;

    Class<?> samBase;
    MethodType samMethodType;

    void foo() {
        samMethodType.toMethodDescriptorString();
        {
            AnnotationVisitor av0 = cw.visitAnnotation("Lcom/github/ruediste/lambdaInspector/LambdaInformation;", true);
            av0.visit("value", Type.getType(samBase));
            av0.visitEnd();
        }
    }
}
