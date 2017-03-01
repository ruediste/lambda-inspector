package com.github.ruediste.lambdaInspector;

import java.util.function.Consumer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class ImplMethodParser<T> {

    private class ClassVisitorImpl extends ClassVisitor {
        private Lambda lambda;

        T result;

        private String owner;

        public ClassVisitorImpl(Lambda lambda) {
            super(Opcodes.ASM5);
            this.lambda = lambda;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName,
                String[] interfaces) {
            this.owner = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (lambda.implementationMethod.getName().equals(name)
                    && Type.getMethodDescriptor(lambda.implementationMethod).equals(desc)) {
                return visitImpl(owner, access, name, desc, signature, exceptions, x -> result = x);
            }
            return null;
        }

    }

    public abstract MethodVisitor visitImpl(String owner, int access, String name, String desc, String signature,
            String[] exceptions, Consumer<T> resultConsumer);

    public T parse(Lambda lambda) {
        ClassReader cr = new ClassReader(
                LambdaInspector.getBytecodeLoader().apply(lambda.implementationMethod.getDeclaringClass()));
        ClassVisitorImpl cv = new ClassVisitorImpl(lambda);
        cr.accept(cv, 0);
        return cv.result;
    }

}
