package com.github.ruediste.lambdaInspector.expr;

import org.objectweb.asm.Opcodes;

public class BinaryArithmeticExpression extends Expression {

    public final Expression exp1;
    public final Expression exp2;
    public final ArithmeticOperation op;

    public enum ArithmeticOperation implements Opcodes {
        ADD("+", IADD, FADD, LADD, DADD) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) + ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) + ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) + ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) + ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) + ((Character) arg2);
                } else if (arg1 instanceof Double) {
                    return ((Double) arg1) + ((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1) + ((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        SUB("-", ISUB, FSUB, LSUB, DSUB) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) - ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) - ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) - ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) - ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) - ((Character) arg2);
                } else if (arg1 instanceof Double) {
                    return ((Double) arg1) - ((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1) - ((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        MUL("*", IMUL, FMUL, LMUL, DMUL) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) * ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) * ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) * ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) * ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) * ((Character) arg2);
                } else if (arg1 instanceof Double) {
                    return ((Double) arg1) * ((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1) * ((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },

        DIV("/", IDIV, FDIV, LDIV, DDIV) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) / ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) / ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) / ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) / ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) / ((Character) arg2);
                } else if (arg1 instanceof Double) {
                    return ((Double) arg1) / ((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1) / ((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        REM("%", IREM, FREM, LREM, DREM) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) % ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) % ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) % ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) % ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) % ((Character) arg2);
                } else if (arg1 instanceof Double) {
                    return ((Double) arg1) % ((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1) % ((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },

        SHL("<<", ISHL, LSHL) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) << ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) << ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) << ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) << ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) << ((Character) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        SHR(">>", ISHR, LSHR) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) >> ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) >> ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) >> ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) >> ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) >> ((Character) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        USHR(">>>", IUSHR, LUSHR) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) >>> ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) >>> ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) >>> ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) >>> ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) >>> ((Character) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },

        AND("&", IAND, LAND) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) & ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) & ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) & ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) & ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) & ((Character) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        OR("|", IOR, LOR) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) | ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) | ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) | ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) | ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) | ((Character) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        XOR("^", IXOR, LXOR) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Long) {
                    return ((Long) arg1) ^ ((Long) arg2);
                } else if (arg1 instanceof Integer) {
                    return ((Integer) arg1) ^ ((Integer) arg2);
                } else if (arg1 instanceof Short) {
                    return ((Short) arg1) ^ ((Short) arg2);
                } else if (arg1 instanceof Byte) {
                    return ((Byte) arg1) ^ ((Byte) arg2);
                } else if (arg1 instanceof Character) {
                    return ((Character) arg1) ^ ((Character) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },

        CMP(Integer.TYPE, "==", LCMP) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                return ((Long) arg1).compareTo((Long) arg2);
            }
        },
        CMPL(Integer.TYPE, "cmpl", FCMPL, DCMPL) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Double) {
                    return ((Double) arg1).compareTo((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1).compareTo((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        },
        CMPG(Integer.TYPE, "cmpg", FCMPG, DCMPG) {
            @Override
            public Object evaluate(Object arg1, Object arg2) {
                if (arg1 instanceof Double) {
                    return ((Double) arg1).compareTo((Double) arg2);
                } else if (arg1 instanceof Float) {
                    return ((Float) arg1).compareTo((Float) arg2);
                }
                throw new UnsupportedOperationException("Unknown argument type: " + arg1);
            }
        };

        public final String symbol;
        private final int[] opcodes;
        public final Class<?> returnType;

        private ArithmeticOperation(Class<?> returnType, String symbol, int... opcodes) {
            this.returnType = returnType;
            this.symbol = symbol;
            this.opcodes = opcodes;
        }

        private ArithmeticOperation(String symbol, int... opcodes) {
            this(null, symbol, opcodes);
        }

        public static ArithmeticOperation byOpcode(int opcode) {
            for (ArithmeticOperation operation : values()) {
                for (int op : operation.opcodes) {
                    if (opcode == op)
                        return operation;
                }
            }
            return null;
        }

        public abstract Object evaluate(Object arg1, Object arg2);
    }

    public BinaryArithmeticExpression(ArithmeticOperation op, Expression exp1, Expression exp2) {
        super(exp1.getType());
        this.op = op;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return exp1 + " " + op.symbol + " " + exp2;
    }

}
