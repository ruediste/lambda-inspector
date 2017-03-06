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
            result.stat = stat;
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
            if (stat.propertyInfo != null) {
                result.property = result.new LambdaPropertyHandle(stat.propertyInfo);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();
    private static LambdaPropertyAnalyzer propertyAnalyzer = new LambdaPropertyAnalyzer();

    /**
     * Inspect static lambda with caching and expression parsing
     */
    public static LambdaStatic inspectStatic(Object lambda) {
        // TODO: cache result
        LambdaStatic stat = inspectStaticNoExpression(lambda);
        stat.expression = analyzer.analyze(stat);
        stat.propertyInfo = propertyAnalyzer.analyze(stat);
        return stat;

    }

    /**
     * Inspect lambda, non-cached, no expression parsing
     */
    public static LambdaStatic inspectStaticNoExpression(Object lambda) {
        try {
            LambdaInformation info = lambda.getClass().getAnnotation(LambdaInformation.class);
            ClassLoader cl = info.implMethodClass().getClassLoader();

            LambdaStatic stat = new LambdaStatic();
            Class<?>[] samArgTypes = getArgumentTypes(cl, Type.getMethodType(info.samMethodDesc()));
            Class<?>[] implArgTypes = getArgumentTypes(cl, Type.getMethodType(info.implMethodDesc()));
            stat.implementationMethod = info.implMethodClass().getDeclaredMethod(info.implMethodName(), implArgTypes);
            stat.argumentTypes = samArgTypes;
            stat.capturedTypes = Arrays.copyOfRange(implArgTypes, 0, implArgTypes.length - samArgTypes.length);
            return stat;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?>[] getArgumentTypes(ClassLoader cl, Type methodType) throws ClassNotFoundException {
        Type[] implArgTypes = methodType.getArgumentTypes();
        Class<?>[] implArgCls = new Class<?>[implArgTypes.length];
        for (int i = 0; i < implArgTypes.length; i++) {
            Type type = implArgTypes[i];
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
        default:
            cls = cl.loadClass(type.getClassName());
        }
        return cls;
    }
}
