package com.github.ruediste.lambdaInspector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import com.github.ruediste.lambdaInspector.expr.ArgumentExpression;
import com.github.ruediste.lambdaInspector.expr.ArrayLengthExpression;
import com.github.ruediste.lambdaInspector.expr.ArrayLoadExpression;
import com.github.ruediste.lambdaInspector.expr.BinaryArithmeticExpression;
import com.github.ruediste.lambdaInspector.expr.BinaryArithmeticExpression.ArithmeticOperation;
import com.github.ruediste.lambdaInspector.expr.CapturedArgExpression;
import com.github.ruediste.lambdaInspector.expr.CastExpression;
import com.github.ruediste.lambdaInspector.expr.ConstExpression;
import com.github.ruediste.lambdaInspector.expr.Expression;
import com.github.ruediste.lambdaInspector.expr.ExpressionBase;
import com.github.ruediste.lambdaInspector.expr.GetFieldExpression;
import com.github.ruediste.lambdaInspector.expr.InstanceOfExpression;
import com.github.ruediste.lambdaInspector.expr.MethodInvocationExpression;
import com.github.ruediste.lambdaInspector.expr.NewArrayExpression;
import com.github.ruediste.lambdaInspector.expr.NewExpression;
import com.github.ruediste.lambdaInspector.expr.ReturnAddressExpression;
import com.github.ruediste.lambdaInspector.expr.ThisExpression;
import com.github.ruediste.lambdaInspector.expr.UnaryExpression;
import com.github.ruediste.lambdaInspector.expr.UnaryExpression.UnaryExpressionType;
import com.github.ruediste.lambdaInspector.expr.UnknownExpression;

public class LambdaAnalyzer {

    private static class ExpressionValue implements Value {

        Expression expr;
        int size;

        public ExpressionValue(int size) {
            this.size = size;

        }

        public ExpressionValue(Expression expr) {
            this.size = Type.getType(expr.getType()).getSize();
            this.expr = expr;
        }

        public ExpressionValue(int size, ExpressionBase expr) {
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

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!obj.getClass().equals(getClass())) {
                return false;
            }
            ExpressionValue other = (ExpressionValue) obj;
            return size == other.size && Objects.equals(expr, other.expr);
        }

        @Override
        public int hashCode() {
            return size;
        }

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
                    return new ExpressionValue(new ConstExpression(null, null));
                case ICONST_M1:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, -1));
                case ICONST_0:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, 0));
                case ICONST_1:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, 1));
                case ICONST_2:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, 2));
                case ICONST_3:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, 3));
                case ICONST_4:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, 4));
                case ICONST_5:
                    return new ExpressionValue(new ConstExpression(Integer.TYPE, 5));
                case LCONST_0:
                    return new ExpressionValue(new ConstExpression(Long.TYPE, 0));
                case LCONST_1:
                    return new ExpressionValue(new ConstExpression(Long.TYPE, 1));
                case FCONST_0:
                    return new ExpressionValue(new ConstExpression(Float.TYPE, Float.valueOf(0)));
                case FCONST_1:
                    return new ExpressionValue(new ConstExpression(Float.TYPE, Float.valueOf(1)));
                case FCONST_2:
                    return new ExpressionValue(new ConstExpression(Float.TYPE, Float.valueOf(2)));
                case DCONST_0:
                    return new ExpressionValue(new ConstExpression(Double.TYPE, Double.valueOf(0)));
                case DCONST_1:
                    return new ExpressionValue(new ConstExpression(Double.TYPE, Double.valueOf(1)));
                case BIPUSH:
                case SIPUSH:
                    return new ExpressionValue(1, new ConstExpression(Integer.TYPE, ((IntInsnNode) insn).operand));
                case LDC:
                    Object cst = ((LdcInsnNode) insn).cst;
                    if (cst instanceof Integer) {
                        return new ExpressionValue(new ConstExpression(Integer.TYPE, cst));
                    } else if (cst instanceof Float) {
                        return new ExpressionValue(new ConstExpression(Float.TYPE, cst));
                    } else if (cst instanceof Long) {
                        return new ExpressionValue(new ConstExpression(Long.TYPE, cst));
                    } else if (cst instanceof Double) {
                        return new ExpressionValue(new ConstExpression(Double.TYPE, cst));
                    } else if (cst instanceof String) {
                        return new ExpressionValue(new ConstExpression(String.class, cst));
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
                    Class<?> owner = LambdaInspector.loadClass(cl, Type.getObjectType(fieldInsn.owner));
                    Field field = owner.getDeclaredField(fieldInsn.name);
                    return new ExpressionValue(new GetFieldExpression(null, field));
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
            ExpressionValue result = value;
            if (insn.getOpcode() == ALOAD) {
                if (value.expr == null) {
                    VarInsnNode varInsn = (VarInsnNode) insn;
                    int argIdx = varInsn.var;
                    if (argIdx == 0 && lambda.this_ != null) {
                        result = new ExpressionValue(
                                new ThisExpression(lambda.implementationMethod.getDeclaringClass()));
                    } else {
                        if (lambda.this_ != null)
                            argIdx--;
                        if (argIdx < lambda.capturedTypes.length) {
                            Class<?> argCls = lambda.capturedTypes[argIdx];
                            Type argType = Type.getType(argCls);
                            result = new ExpressionValue(argType.getSize(), new CapturedArgExpression(argCls, argIdx));
                        } else {
                            argIdx -= lambda.capturedTypes.length;
                            Class<?> argCls = lambda.argumentTypes[argIdx];
                            Type argType = Type.getType(argCls);
                            result = new ExpressionValue(argType.getSize(), new ArgumentExpression(argCls, argIdx));
                        }
                    }
                }
            }
            setExpressionValue(insn, result);
            return result;
        }

        @Override
        public ExpressionValue unaryOperation(AbstractInsnNode insn, ExpressionValue value) throws AnalyzerException {
            try {
                ExpressionValue result;
                UnaryExpressionType unaryType = UnaryExpressionType.getTypeByOpcode(insn.getOpcode());
                if (unaryType != null) {
                    result = new ExpressionValue(Type.getType(unaryType.getResultType()).getSize(),
                            new UnaryExpression(unaryType, value.expr));
                } else
                    switch (insn.getOpcode()) {
                    case IFEQ:
                    case IFNE:
                    case IFLT:
                    case IFGE:
                    case IFGT:
                    case IFLE:
                    case TABLESWITCH:
                    case LOOKUPSWITCH:
                    case IRETURN:
                    case LRETURN:
                    case FRETURN:
                    case DRETURN:
                    case ARETURN:
                    case PUTSTATIC:
                    case MONITORENTER:
                    case MONITOREXIT:
                    case IFNULL:
                    case IFNONNULL:
                        setExpressionValue(insn, value);
                        return null;
                    case GETFIELD: {
                        FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                        Type type = Type.getType(fieldInsn.desc);
                        Field field = LambdaInspector.loadClass(cl, Type.getObjectType(fieldInsn.owner))
                                .getDeclaredField(fieldInsn.name);
                        result = new ExpressionValue(type.getSize(), new GetFieldExpression(value.expr, field));
                        break;
                    }
                    case NEWARRAY: {
                        Class<?> arrayType;
                        switch (((IntInsnNode) insn).operand) {
                        case T_BOOLEAN:
                            arrayType = Class.forName("[Z");
                            break;
                        case T_CHAR:
                            arrayType = Class.forName("[C");
                            break;
                        case T_BYTE:
                            arrayType = Class.forName("[B");
                            break;
                        case T_SHORT:
                            arrayType = Class.forName("[S");
                            break;
                        case T_INT:
                            arrayType = Class.forName("[I");
                            break;
                        case T_FLOAT:
                            arrayType = Class.forName("[F");
                            break;
                        case T_DOUBLE:
                            arrayType = Class.forName("[D");
                            break;
                        case T_LONG:
                            arrayType = Class.forName("[J");
                            break;
                        default:
                            throw new AnalyzerException(insn, "Invalid array type");
                        }
                        result = new ExpressionValue(1, new NewArrayExpression(arrayType, value.expr, false));
                        break;
                    }
                    case ANEWARRAY: {
                        String desc = ((TypeInsnNode) insn).desc;
                        Type type = Type.getObjectType(desc);
                        result = new ExpressionValue(1, new NewArrayExpression(
                                Class.forName("[L" + type.getClassName() + ";"), value.expr, false));
                        break;
                    }
                    case ARRAYLENGTH:
                        result = new ExpressionValue(new ArrayLengthExpression(value.expr));
                        break;
                    case ATHROW:
                        setExpressionValue(insn, value);
                        return null;
                    case CHECKCAST: {
                        String desc = ((TypeInsnNode) insn).desc;
                        Type type = Type.getObjectType(desc);
                        result = new ExpressionValue(
                                new CastExpression(LambdaInspector.loadClass(cl, type), value.expr));
                        break;
                    }
                    case INSTANCEOF: {
                        String desc = ((TypeInsnNode) insn).desc;
                        Type type = Type.getObjectType(desc);
                        result = new ExpressionValue(
                                new InstanceOfExpression(value.expr, LambdaInspector.loadClass(cl, type)));
                        break;

                    }
                    default:
                        throw new Error("Internal error.");
                    }
                setExpressionValue(insn, result);
                return result;
            } catch (AnalyzerException e) {
                throw e;
            } catch (Exception e) {
                throw new AnalyzerException(insn, "Error occurred", e);
            }
        }

        @Override
        public ExpressionValue binaryOperation(AbstractInsnNode insn, ExpressionValue value1, ExpressionValue value2)
                throws AnalyzerException {
            Expression result;
            ArithmeticOperation op = BinaryArithmeticExpression.ArithmeticOperation.byOpcode(insn.getOpcode());
            if (op != null) {
                result = new BinaryArithmeticExpression(op, value1.expr, value2.expr);
            } else
                switch (insn.getOpcode()) {
                case IALOAD:
                case BALOAD:
                case CALOAD:
                case SALOAD:
                case LALOAD:
                case DALOAD:
                case FALOAD:
                case AALOAD:
                    result = new ArrayLoadExpression(value1.expr, value2.expr);
                    break;
                case IF_ICMPEQ:
                case IF_ICMPNE:
                case IF_ICMPLT:
                case IF_ICMPGE:
                case IF_ICMPGT:
                case IF_ICMPLE:
                case IF_ACMPEQ:
                case IF_ACMPNE:
                case PUTFIELD:
                    return null;
                default:
                    throw new Error("Internal error.");
                }

            ExpressionValue expr = new ExpressionValue(result);
            setExpressionValue(insn, expr);
            return expr;
        }

        @Override
        public ExpressionValue ternaryOperation(AbstractInsnNode insn, ExpressionValue value1, ExpressionValue value2,
                ExpressionValue value3) throws AnalyzerException {
            switch (insn.getOpcode()) {
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                return null;
            default:
                throw new UnsupportedOperationException("Illegal opcode " + insn.getOpcode());
            }
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
                    Executable method;
                    if ("<init>".equals(methodInsn.name)) {
                        method = owner.getDeclaredConstructor(argTypes);
                    } else
                        method = owner.getDeclaredMethod(methodInsn.name, argTypes);
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
                throw new RuntimeException(e);
            }
            setExpressionValue(insn, result);
            return result;
        }

        private void setExpressionValue(AbstractInsnNode insn, ExpressionValue result) {
            expressions[instructions.indexOf(insn)] = result;
        }

        @Override
        public void returnOperation(AbstractInsnNode insn, ExpressionValue value, ExpressionValue expected)
                throws AnalyzerException {
            // NOP
        }

        @Override
        public ExpressionValue merge(ExpressionValue v, ExpressionValue w) {
            if (v.expr != null) {
                return new ExpressionValue(v.size, new UnknownExpression(v.expr.getType()));
            } else if (w.expr != null) {
                return new ExpressionValue(w.size, new UnknownExpression(w.expr.getType()));
            } else
                return new ExpressionValue(v.size, null);
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
                            List<Expression> results = new ArrayList<>();
                            Frame<ExpressionValue>[] frames = a.analyze(owner, node);
                            for (int i = 0; i < frames.length; i++) {
                                Frame<ExpressionValue> frame = frames[i];
                                if (frame == null)
                                    continue;
                                Node<ExpressionValue> fNode = (Node<ExpressionValue>) frame;
                                int idx = i;
                                if (fNode.successors.isEmpty()) {
                                    while (true) {
                                        if (expressions[idx] != null) {
                                            results.add(expressions[idx].expr);
                                            break;
                                        }

                                        if (fNode.predecessors.size() != 1)
                                            break;
                                        idx = fNode.predecessors.iterator().next();
                                        fNode = (Node<ExpressionValue>) frames[idx];
                                    }

                                }
                            }
                            if (results.size() == 1)
                                resultConsumer.accept(results.get(0));
                        } catch (AnalyzerException e) {
                            throw new RuntimeException(e);
                        }
                    };
                };
            }
        }.parse(lambda);
    }

}
