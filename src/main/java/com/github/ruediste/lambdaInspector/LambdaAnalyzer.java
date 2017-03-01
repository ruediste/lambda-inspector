package com.github.ruediste.lambdaInspector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import com.github.ruediste.lambdaInspector.expr.ConstExpression;
import com.github.ruediste.lambdaInspector.expr.Expression;
import com.github.ruediste.lambdaInspector.expr.GetFieldExpression;
import com.github.ruediste.lambdaInspector.expr.MethodInvocationExpression;
import com.github.ruediste.lambdaInspector.expr.NewExpression;
import com.github.ruediste.lambdaInspector.expr.ReturnAddressExpression;

public class LambdaAnalyzer {

    private static class ExpressionValue implements Value {

        Expression expr;
        int size;

        public ExpressionValue(int size) {
            this.size = size;

        }

        public ExpressionValue(int size, Expression expr) {
            this.size = size;
            this.expr = expr;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "(" + size + ":" + expr + ")";
        }

        // @Override
        // public boolean equals(Object obj) {
        // if (this == obj)
        // return true;
        // if (obj == null)
        // return false;
        // if (!obj.getClass().equals(getClass())) {
        // return false;
        // }
        // ExpressionValue other = (ExpressionValue) obj;
        // return size == other.size;
        // }
        //
        // @Override
        // public int hashCode() {
        // return size;
        // }

    }

    private static class ExpressionInterpreter extends Interpreter<ExpressionValue> implements Opcodes {

        private Lambda lambda;
        private ClassLoader cl;
        private ExpressionValue[] expressions;
        private InsnList instructions;

        protected ExpressionInterpreter(Lambda lambda, ExpressionValue[] expressions, InsnList instructions) {
            super(Opcodes.ASM5);
            this.lambda = lambda;
            this.expressions = expressions;
            this.instructions = instructions;
            cl = lambda.implementationMethod.getDeclaringClass().getClassLoader();
        }

        @Override
        public ExpressionValue newValue(Type type) {

            if (type == Type.VOID_TYPE) {
                return null;
            }
            return new ExpressionValue(type == null ? 1 : type.getSize());
        }

        @Override
        public ExpressionValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
            try {
                switch (insn.getOpcode()) {
                case ACONST_NULL:
                    return new ExpressionValue(1, new ConstExpression(null, null));
                case ICONST_M1:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, -1));
                case ICONST_0:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, 0));
                case ICONST_1:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, 1));
                case ICONST_2:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, 2));
                case ICONST_3:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, 3));
                case ICONST_4:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, 4));
                case ICONST_5:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, 5));
                case LCONST_0:
                    return new ExpressionValue(2, new ConstExpression(Long.TYPE, 0));
                case LCONST_1:
                    return new ExpressionValue(2, new ConstExpression(Long.TYPE, 1));
                case FCONST_0:
                    return new ExpressionValue(1, new ConstExpression(Float.TYPE, Float.valueOf(0)));
                case FCONST_1:
                    return new ExpressionValue(1, new ConstExpression(Float.TYPE, Float.valueOf(1)));
                case FCONST_2:
                    return new ExpressionValue(1, new ConstExpression(Float.TYPE, Float.valueOf(2)));
                case DCONST_0:
                    return new ExpressionValue(2, new ConstExpression(Float.TYPE, Double.valueOf(0)));
                case DCONST_1:
                    return new ExpressionValue(2, new ConstExpression(Float.TYPE, Double.valueOf(1)));
                case BIPUSH:
                case SIPUSH:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, ((IntInsnNode) insn).operand));
                case LDC:
                    Object cst = ((LdcInsnNode) insn).cst;
                    if (cst instanceof Integer) {
                        return new ExpressionValue(1, new ConstExpression(Integer.TYPE, cst));
                    } else if (cst instanceof Float) {
                        return new ExpressionValue(1, new ConstExpression(Float.TYPE, cst));
                    } else if (cst instanceof Long) {
                        return new ExpressionValue(2, new ConstExpression(Long.TYPE, cst));
                    } else if (cst instanceof Double) {
                        return new ExpressionValue(2, new ConstExpression(Double.TYPE, cst));
                    } else if (cst instanceof String) {
                        return new ExpressionValue(1, new ConstExpression(String.class, cst));
                    } else if (cst instanceof Type) {
                        int sort = ((Type) cst).getSort();
                        if (sort == Type.OBJECT || sort == Type.ARRAY) {
                            return new ExpressionValue(1, new ConstExpression(Class.class, cst));
                        } else if (sort == Type.METHOD) {
                            return new ExpressionValue(1, new ConstExpression(MethodType.class, cst));
                        } else {
                            throw new IllegalArgumentException("Illegal LDC constant " + cst);
                        }
                    } else if (cst instanceof Handle) {
                        return new ExpressionValue(1, new ConstExpression(MethodHandle.class, cst));
                    } else {
                        throw new IllegalArgumentException("Illegal LDC constant " + cst);
                    }
                case JSR:
                    return new ExpressionValue(1, new ReturnAddressExpression());
                case GETSTATIC: {
                    FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                    Type type = Type.getType(fieldInsn.desc);
                    Class<?> owner = LambdaInspector.loadClass(cl, Type.getObjectType(fieldInsn.owner));
                    Field field = owner.getDeclaredField(fieldInsn.name);
                    return new ExpressionValue(type.getSize(), new GetFieldExpression(null, field));
                }
                case NEW: {
                    TypeInsnNode typeInsn = (TypeInsnNode) insn;
                    return new ExpressionValue(1,
                            new NewExpression(LambdaInspector.loadClass(cl, Type.getObjectType(typeInsn.desc))));
                }
                default:
                    throw new Error("Internal error.");
                }
            } catch (Exception e) {
                throw new AnalyzerException(insn, "Error", e);
            }
        }

        @Override
        public ExpressionValue copyOperation(AbstractInsnNode insn, ExpressionValue value) throws AnalyzerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExpressionValue unaryOperation(AbstractInsnNode insn, ExpressionValue value) throws AnalyzerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExpressionValue binaryOperation(AbstractInsnNode insn, ExpressionValue value1, ExpressionValue value2)
                throws AnalyzerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExpressionValue ternaryOperation(AbstractInsnNode insn, ExpressionValue value1, ExpressionValue value2,
                ExpressionValue value3) throws AnalyzerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExpressionValue naryOperation(AbstractInsnNode insn, List<? extends ExpressionValue> values)
                throws AnalyzerException {
            ExpressionValue result;
            try {
                if (insn.getOpcode() == INVOKEDYNAMIC) {
                    InvokeDynamicInsnNode dynamicInsn = (InvokeDynamicInsnNode) insn;
                    throw new UnsupportedOperationException();
                } else if (insn.getOpcode() == MULTIANEWARRAY) {
                    MultiANewArrayInsnNode arrayInsn = (MultiANewArrayInsnNode) insn;
                    throw new UnsupportedOperationException();
                } else {
                    MethodInsnNode methodInsn = (MethodInsnNode) insn;
                    Class<?> owner = LambdaInspector.loadClass(cl, Type.getObjectType(methodInsn.owner));
                    Type methodType = Type.getMethodType(methodInsn.desc);
                    Class<?>[] argTypes = LambdaInspector.getArgumentTypes(cl, methodType);
                    Method method = owner.getDeclaredMethod(methodInsn.name, argTypes);
                    boolean isStatic = Modifier.isStatic(method.getModifiers());
                    List<Expression> args = new ArrayList<>();
                    Expression target = null;
                    int idx = 0;
                    if (!isStatic)
                        target = values.get(idx++).expr;
                    for (; idx < values.size(); idx++)
                        args.add(values.get(idx).expr);
                    result = new ExpressionValue(methodType.getReturnType().getSize(),
                            new MethodInvocationExpression(method, target, args));
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
            expressions[instructions.indexOf(insn)] = result;
            return result;
        }

        @Override
        public void returnOperation(AbstractInsnNode insn, ExpressionValue value, ExpressionValue expected)
                throws AnalyzerException {
            throw new UnsupportedOperationException();

        }

        @Override
        public ExpressionValue merge(ExpressionValue v, ExpressionValue w) {
            throw new UnsupportedOperationException();
        }

    }

    private static class Node<V extends Value> extends Frame<V> {
        Set<Integer> successors = new HashSet<>();
        Set<Integer> predecessors = new HashSet<>();

        public Node(int nLocals, int nStack) {
            super(nLocals, nStack);
        }

        public Node(Frame<? extends V> src) {
            super(src);
        }

        @Override
        public void execute(AbstractInsnNode insn, Interpreter<V> interpreter) throws AnalyzerException {
            super.execute(insn, interpreter);
        }
    }

    public Expression analyze(Lambda lambda) {
        return new ImplMethodParser<Expression>() {

            @Override
            public MethodVisitor visitImpl(String owner, int access, String name, String desc, String signature,
                    String[] exceptions, Consumer<Expression> resultConsumer) {

                MethodNode node = new MethodNode(access, name, desc, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, node) {
                    @Override
                    public void visitEnd() {
                        InsnList instructions = node.instructions;
                        ExpressionValue[] expressions = new ExpressionValue[instructions.size()];
                        Analyzer<ExpressionValue> a = new Analyzer<ExpressionValue>(
                                new ExpressionInterpreter(lambda, expressions, instructions)) {
                            @Override
                            protected Frame<ExpressionValue> newFrame(int nLocals, int nStack) {
                                return new Node<ExpressionValue>(nLocals, nStack);
                            }

                            @Override
                            protected Frame<ExpressionValue> newFrame(Frame<? extends ExpressionValue> src) {
                                return new Node<ExpressionValue>(src);
                            }

                            @Override
                            protected void newControlFlowEdge(int src, int dst) {
                                Node<ExpressionValue> srcNode = (Node<ExpressionValue>) getFrames()[src];
                                Node<ExpressionValue> dstNode = (Node<ExpressionValue>) getFrames()[dst];
                                srcNode.successors.add(dst);
                                dstNode.predecessors.add(src);
                            }
                        };
                        try {
                            Frame<ExpressionValue>[] frames = a.analyze(owner, node);
                            for (int i = 0; i < frames.length; i++) {
                                Frame<ExpressionValue> frame = frames[i];
                                Node<ExpressionValue> fNode = (Node<ExpressionValue>) frame;
                                int idx = i;
                                if (fNode.successors.isEmpty()) {
                                    while (true) {
                                        if (expressions[idx] != null)
                                            resultConsumer.accept(expressions[idx].expr);
                                        if (fNode.predecessors.size() != 1)
                                            break;
                                        idx = fNode.predecessors.iterator().next();
                                        fNode = (Node<ExpressionValue>) frames[idx];
                                    }

                                }
                            }
                        } catch (AnalyzerException e) {
                            throw new RuntimeException(e);
                        }
                    };
                };
            }
        }.parse(lambda);
    }

}
