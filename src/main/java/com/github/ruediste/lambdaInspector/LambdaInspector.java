package com.github.ruediste.lambdaInspector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

import org.objectweb.asm.Type;

import com.ea.agentloader.AgentLoader;

public class LambdaInspector {

    private static Function<Class<?>, byte[]> bytecodeLoader = cls -> {
        try (InputStream in = cls.getClassLoader().getResourceAsStream(cls.getName().replace('.', '/') + ".class")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bb = new byte[1024];
            int read;
            while ((read = in.read(bb)) > 0) {
                baos.write(bb, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while loading bytecode for " + cls);
        }
    };

    public static Function<Class<?>, byte[]> getBytecodeLoader() {
        return bytecodeLoader;
    }

    private static boolean initialized;

    public static synchronized void setup() {
        if (!initialized) {
            AgentLoader.loadAgentClass(LambdaInspectorAgent.class.getName(), "");
            initialized = true;
        }
    }

    public static void setup(Function<Class<?>, byte[]> bytecodeLoader) {
        setup();
        LambdaInspector.bytecodeLoader = bytecodeLoader;
    }

    /**
     * Inspect lambda with expression parsing and caching
     */
    public static Lambda inspect(Object lambda) {
        LambdaStatic stat = inspectStatic(lambda);
        return inspect(lambda, stat);
    }

    /**
     * Inspect lambda without caching
     */
    public static Lambda inspect(Object lambda, LambdaStatic stat) {
        try {
            Lambda result = new Lambda();
            result.static_ = stat;
            boolean implStatic = Modifier.isStatic(inspectStatic(lambda).implementationMethod.getModifiers());
            result.captured = new Object[inspectStatic(lambda).capturedTypes.length];
            {
                int offset = implStatic ? 0 : 1;
                for (int i = 0; i < result.captured.length + offset; i++) {

                    Field field = lambda.getClass().getDeclaredField("arg$" + (i + 1));
                    field.setAccessible(true);
                    Object value = field.get(lambda);
                    if (!implStatic && i == 0)
                        result.this_ = value;
                    else
                        result.captured[i - offset] = value;
                }
            }
            if (stat.accessedMemberInfo != null) {
                result.memberHandle = result.new LambdaAccessedMemberHandle();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static LambdaExpressionAnalyzer expressionAnalyzer = new LambdaExpressionAnalyzer();

    /**
     * Inspect static lambda with caching and expression parsing
     */
    public static LambdaStatic inspectStatic(Object lambda) {
        LambdaStatic stat = inspectStaticNoExpression(lambda);
        stat.expression = expressionAnalyzer.analyze(stat);
        stat.accessedMemberInfo = LambdaAccessedMemberAnalyzer.analyze(stat);
        return stat;

    }

    /**
     * Determine if a lambda can be inspected
     */
    public static boolean canBeInspected(Object lambda) {
        return lambda.getClass().isAnnotationPresent(LambdaInformation.class);
    }

    /**
     * Inspect lambda, non-cached, no expression parsing
     */
    public static LambdaStatic inspectStaticNoExpression(Object lambda) {
        try {
            LambdaInformation info = lambda.getClass().getAnnotation(LambdaInformation.class);
            if (info == null)
                throw new RuntimeException(
                        "Unable to get LambaInformation annotation on lambda object. Did you call LambdaInspector.setup()?");
            ClassLoader cl = info.implMethodClass().getClassLoader();

            LambdaStatic stat = new LambdaStatic();
            Class<?>[] samArgTypes = loadClasses(cl, Type.getMethodType(info.samMethodDesc()).getArgumentTypes());
            Class<?>[] implArgTypes = loadClasses(cl, Type.getMethodType(info.implMethodDesc()).getArgumentTypes());
            stat.implementationMethod = info.implMethodClass().getDeclaredMethod(info.implMethodName(), implArgTypes);
            stat.argumentTypes = samArgTypes;
            stat.capturedTypes = Arrays.copyOfRange(implArgTypes, 0, implArgTypes.length - samArgTypes.length);
            return stat;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?>[] loadClasses(ClassLoader cl, Type[] types) throws ClassNotFoundException {
        Class<?>[] implArgCls = new Class<?>[types.length];
        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            Class<?> cls = loadClass(cl, type);
            implArgCls[i] = cls;
        }
        return implArgCls;
    }

    public static Class<?> loadClass(ClassLoader cl, Type type) throws ClassNotFoundException {
        Class<?> cls;
        switch (type.getSort()) {
        case Type.BOOLEAN:
            cls = Boolean.TYPE;
            break;
        case Type.CHAR:
            cls = Character.TYPE;
            break;
        case Type.BYTE:
            cls = Byte.TYPE;
            break;
        case Type.SHORT:
            cls = Short.TYPE;
            break;
        case Type.INT:
            cls = Integer.TYPE;
            break;
        case Type.FLOAT:
            cls = Float.TYPE;
            break;
        case Type.LONG:
            cls = Long.TYPE;
            break;
        case Type.DOUBLE:
            cls = Double.TYPE;
            break;
        case Type.ARRAY: {
            cls = Class.forName("[L" + type.getElementType().getClassName() + ";", true, cl);
            break;
        }
        default:
            cls = cl.loadClass(type.getClassName());
        }
        return cls;
    }
}
