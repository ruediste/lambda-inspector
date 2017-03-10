package com.github.ruediste.lambdaInspector.expr;

import org.objectweb.asm.Opcodes;

public class UnaryExpression extends Expression {

    public final Expression argument;
    public final UnaryExpressionType type;

    public enum UnaryExpressionType {
        INEG(Integer.TYPE, Opcodes.INEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }

            @Override
            public Object eval(Object arg) {
                return -((Integer) arg);
            }
        },
        IINC(Integer.TYPE, Opcodes.IINC) {
            @Override
            String toString(UnaryExpression expr) {
                return expr + "++";
            }

            @Override
            public Object eval(Object arg) {
                return ((Integer) arg) + 1;
            }
        },
        L2I(Integer.TYPE, Opcodes.L2I) {
            @Override
            String toString(UnaryExpression expr) {
                return "((int)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return ((Long) arg).intValue();
            }
        },
        F2I(Integer.TYPE, Opcodes.F2I) {
            @Override
            String toString(UnaryExpression expr) {
                return "((int)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return ((Float) arg).intValue();
            }
        },
        D2I(Integer.TYPE, Opcodes.D2I) {
            @Override
            String toString(UnaryExpression expr) {
                return "((int)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return ((Double) arg).intValue();
            }
        },
        I2B(Integer.TYPE, Opcodes.I2B) {
            @Override
            String toString(UnaryExpression expr) {
                return "((byte)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return ((Integer) arg).byteValue();
            }
        },
        I2C(Integer.TYPE, Opcodes.I2C) {
            @Override
            String toString(UnaryExpression expr) {
                return "((char)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (char) (int) ((Integer) arg);
            }
        },
        I2S(Integer.TYPE, Opcodes.I2S) {
            @Override
            String toString(UnaryExpression expr) {
                return "((short)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (short) (int) ((Integer) arg);
            }
        },

        FNEG(Float.TYPE, Opcodes.FNEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }

            @Override
            public Object eval(Object arg) {
                return -((Float) arg);
            }
        },
        I2F(Float.TYPE, Opcodes.I2F) {
            @Override
            String toString(UnaryExpression expr) {
                return "((float)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (float) ((Integer) arg);
            }
        },
        L2F(Float.TYPE, Opcodes.L2F) {
            @Override
            String toString(UnaryExpression expr) {
                return "((float)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (float) ((Long) arg);
            }
        },
        D2F(Float.TYPE, Opcodes.D2F) {
            @Override
            String toString(UnaryExpression expr) {
                return "((float)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (float) (double) ((Double) arg);
            }
        },

        LNEG(Long.TYPE, Opcodes.LNEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }

            @Override
            public Object eval(Object arg) {
                return -((Long) arg);
            }
        },
        I2L(Long.TYPE, Opcodes.I2L) {
            @Override
            String toString(UnaryExpression expr) {
                return "((long)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (long) ((Integer) arg);
            }
        },
        F2L(Long.TYPE, Opcodes.F2L) {
            @Override
            String toString(UnaryExpression expr) {
                return "((long)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (long) (float) ((Float) arg);
            }
        },
        D2L(Long.TYPE, Opcodes.D2L) {
            @Override
            String toString(UnaryExpression expr) {
                return "((long)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (long) (double) ((Double) arg);
            }
        },

        DNEG(Double.TYPE, Opcodes.DNEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }

            @Override
            public Object eval(Object arg) {
                return -((Double) arg);
            }
        },
        I2D(Double.TYPE, Opcodes.I2D) {
            @Override
            String toString(UnaryExpression expr) {
                return "((double)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (double) ((Integer) arg);
            }
        },
        L2D(Double.TYPE, Opcodes.L2D) {
            @Override
            String toString(UnaryExpression expr) {
                return "((double)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (double) ((Long) arg);
            }
        },
        F2D(Double.TYPE, Opcodes.F2D) {
            @Override
            String toString(UnaryExpression expr) {
                return "((double)" + expr + ")";
            }

            @Override
            public Object eval(Object arg) {
                return (double) ((Float) arg);
            }
        },

        ;

        private final Class<?> resultType;
        private final Integer opcode;

        private UnaryExpressionType(Class<?> resultType) {
            this(resultType, null);
        }

        abstract String toString(UnaryExpression expr);

        private UnaryExpressionType(Class<?> resultType, Integer opcode) {
            this.resultType = resultType;
            this.opcode = opcode;
        }

        public Class<?> getResultType() {
            return resultType;
        }

        public Integer getOpcode() {
            return opcode;
        }

        public static UnaryExpressionType getTypeByOpcode(int opcode) {
            for (UnaryExpressionType type : values()) {
                if (type.opcode != null && type.opcode == opcode) {
                    return type;
                }
            }
            return null;
        }

        abstract public Object eval(Object arg);

    }

    public UnaryExpression(UnaryExpressionType type, Expression argument) {
        super(type.getResultType());
        this.type = type;
        this.argument = argument;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression getArgument() {
        return argument;
    }

    public UnaryExpressionType getExpType() {
        return type;
    }
}
