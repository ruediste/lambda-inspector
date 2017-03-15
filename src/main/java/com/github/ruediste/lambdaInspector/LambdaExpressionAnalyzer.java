package com.github.ruediste.lambdaInspector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
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

public class LambdaExpressionAnalyzer {

    private static Constructor<Lookup> lookupConstructor;
    static {
        try {
            lookupConstructor = Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
            lookupConstructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

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

        private LambdaStatic lambda;
        private ClassLoader cl;
        private ExpressionValue[] expressions;
        private InsnList instructions;

        protected ExpressionInterpreter(LambdaStatic lambda, ExpressionValue[] expressions, InsnList instructions) {
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
                    Class<?> fieldType = LambdaInspector.loadClass(cl, Type.getType(fieldInsn.desc));
                    Field field = MethodHandles.reflectAs(Field.class,
                            getLookup(owner).findStaticGetter(owner, fieldInsn.name, fieldType));
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

        private Lookup getLookup(Class<?> owner)
                throws InstantiationException, IllegalAccessException, InvocationTargetException {
            return lookupConstructor.newInstance(owner, 15);
        }

        @Override
        public ExpressionValue copyOperation(AbstractInsnNode insn, ExpressionValue value) throws AnalyzerException {
            ExpressionValue result = value;
            switch (insn.getOpcode()) {
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD: {
                if (value.expr == null) {
                    VarInsnNode varInsn = (VarInsnNode) insn;
                    int argIdx = varInsn.var;
                    boolean isStatic = Modifier.isStatic(lambda.implementationMethod.getModifiers());
                    if (argIdx == 0 && !isStatic) {
                        result = new ExpressionValue(
                                new ThisExpression(lambda.implementationMethod.getDeclaringClass()));
                    } else {
                        if (!isStatic)
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
                break;
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
            case DUP:
            case DUP_X1:
            case DUP_X2:
            case DUP2:
            case DUP2_X1:
            case DUP2_X2:
            case SWAP:
                break;
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
                        Class<?> owner = LambdaInspector.loadClass(cl, Type.getObjectType(fieldInsn.owner));
                        Class<?> fieldType = LambdaInspector.loadClass(cl, Type.getType(fieldInsn.desc));
                        Field field = MethodHandles.reflectAs(Field.class,
                                getLookup(owner).findGetter(owner, fieldInsn.name, fieldType));

                        result = new ExpressionValue(new GetFieldExpression(value.expr, field));
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
                                Class.forName("[L" + type.getClassName() + ";", true, cl), value.expr, false));
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
                    methodType.getReturnType().getSort();
                    new String();
                    Class<?>[] argTypes = LambdaInspector.loadClasses(cl, methodType.getArgumentTypes());

                    boolean isStatic = insn.getOpcode() == INVOKESTATIC;
                    List<Expression> args = new ArrayList<>();
                    Expression target = null;
                    int idx = 0;
                    if (!isStatic)
                        target = values.get(idx++).expr;
                    for (; idx < values.size(); idx++)
                        args.add(values.get(idx).expr);
                    if ("<init>".equals(methodInsn.name)) {
                        result = values.get(0);
                        NewExpression newExpr = (NewExpression) target;
                        newExpr.constructor = owner.getDeclaredConstructor(argTypes);
                        newExpr.args = args;
                    } else {

                        Lookup lookup = getLookup(owner);

                        MethodHandle handle;
                        if (isStatic)
                            handle = lookup.findStatic(owner, methodInsn.name,
                                    MethodType.fromMethodDescriptorString(methodInsn.desc, cl));
                        else
                            handle = lookup.findVirtual(owner, methodInsn.name,
                                    MethodType.fromMethodDescriptorString(methodInsn.desc, cl));
                        Method method = MethodHandles.reflectAs(Method.class, handle);
                        // Method method =
                        // owner.getDeclaredMethod(methodInsn.name, argTypes);
                        result = new ExpressionValue(methodType.getReturnType().getSize(),
                                new MethodInvocationExpression(method, target, args));
                    }
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

    private class LambdaImplClassVisitor extends ClassVisitor {
        private LambdaStatic lambda;

        Expression result;

        private String owner;

        public LambdaImplClassVisitor(LambdaStatic lambda) {
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

                        // Iterate over all instructions and search those
                        // without successors. This must be
                        // return or throw expressions. For each such
                        // instruction determin the expression that was
                        // returned
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
                                result = results.get(0);
                        } catch (AnalyzerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }
            return null;
        }

    }

    public Expression analyze(LambdaStatic lambda) {
        if (lambda.implementationMethod.isSynthetic()) {
            ClassReader cr = new ClassReader(
                    LambdaInspector.getBytecodeLoader().apply(lambda.implementationMethod.getDeclaringClass()));
            LambdaImplClassVisitor cv = new LambdaImplClassVisitor(lambda);
            cr.accept(cv, 0);
            return cv.result;
        } else {
            Expression target = null;
            if (!Modifier.isStatic(lambda.implementationMethod.getModifiers())) {
                target = new ThisExpression(lambda.implementationMethod.getDeclaringClass());
            }
            ArrayList<Expression> args = new ArrayList<>();
            for (int i = 0; i < lambda.argumentTypes.length; i++) {
                args.add(new ArgumentExpression(lambda.argumentTypes[i], i));
            }
            return new MethodInvocationExpression(lambda.implementationMethod, target, args);
        }
    }

}
