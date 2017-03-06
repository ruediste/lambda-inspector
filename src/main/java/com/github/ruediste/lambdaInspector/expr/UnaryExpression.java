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
        },
        IINC(Integer.TYPE, Opcodes.IINC) {
            @Override
            String toString(UnaryExpression expr) {
                return expr + "++";
            }
        },
        L2I(Integer.TYPE, Opcodes.L2I) {
            @Override
            String toString(UnaryExpression expr) {
                return "((int)" + expr + ")";
            }
        },
        F2I(Integer.TYPE, Opcodes.F2I) {
            @Override
            String toString(UnaryExpression expr) {
                return "((int)" + expr + ")";
            }
        },
        D2I(Integer.TYPE, Opcodes.D2I) {
            @Override
            String toString(UnaryExpression expr) {
                return "((int)" + expr + ")";
            }
        },
        I2B(Integer.TYPE, Opcodes.I2B) {
            @Override
            String toString(UnaryExpression expr) {
                return "((boolean)" + expr + ")";
            }
        },
        I2C(Integer.TYPE, Opcodes.I2C) {
            @Override
            String toString(UnaryExpression expr) {
                return "((char)" + expr + ")";
            }
        },
        I2S(Integer.TYPE, Opcodes.I2S) {
            @Override
            String toString(UnaryExpression expr) {
                return "((short)" + expr + ")";
            }
        },

        FNEG(Float.TYPE, Opcodes.FNEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }
        },
        I2F(Float.TYPE, Opcodes.I2F) {
            @Override
            String toString(UnaryExpression expr) {
                return "((float)" + expr + ")";
            }
        },
        L2F(Float.TYPE, Opcodes.L2F) {
            @Override
            String toString(UnaryExpression expr) {
                return "((float)" + expr + ")";
            }
        },
        D2F(Float.TYPE, Opcodes.D2F) {
            @Override
            String toString(UnaryExpression expr) {
                return "((float)" + expr + ")";
            }
        },

        LNEG(Long.TYPE, Opcodes.LNEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }
        },
        I2L(Long.TYPE, Opcodes.I2L) {
            @Override
            String toString(UnaryExpression expr) {
                return "((long)" + expr + ")";
            }
        },
        F2L(Long.TYPE, Opcodes.F2L) {
            @Override
            String toString(UnaryExpression expr) {
                return "((long)" + expr + ")";
            }
        },
        D2L(Long.TYPE, Opcodes.D2L) {
            @Override
            String toString(UnaryExpression expr) {
                return "((long)" + expr + ")";
            }
        },

        DNEG(Double.TYPE, Opcodes.DNEG) {
            @Override
            String toString(UnaryExpression expr) {
                return "-" + expr;
            }
        },
        I2D(Double.TYPE, Opcodes.I2D) {
            @Override
            String toString(UnaryExpression expr) {
                return "((double)" + expr + ")";
            }
        },
        L2D(Double.TYPE, Opcodes.L2D) {
            @Override
            String toString(UnaryExpression expr) {
                return "((double)" + expr + ")";
            }
        },
        F2D(Double.TYPE, Opcodes.F2D) {
            @Override
            String toString(UnaryExpression expr) {
                return "((double)" + expr + ")";
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
