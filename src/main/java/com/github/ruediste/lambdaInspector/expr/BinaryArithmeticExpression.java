package com.github.ruediste.lambdaInspector.expr;

import org.objectweb.asm.Opcodes;

public class BinaryArithmeticExpression extends ExpressionBase {

    public final Expression exp1;
    public final Expression exp2;
    public final ArithmeticOperation op;

    public enum ArithmeticOperation implements Opcodes {
        ADD("+", IADD, FADD, LADD, DADD), SUB("-", ISUB, FSUB, LSUB, DSUB), MUL("*", IMUL, FMUL, LMUL, DMUL),

        DIV("/", IDIV, FDIV, LDIV, DDIV), REM("%", IREM, FREM, LREM, DREM),

        SHL("<<", ISHL, LSHL), SHR(">>", ISHR, LSHR), USHR(">>>", IUSHR, LUSHR),

        AND("&", IAND, LAND), OR("|", IOR, LOR), XOR("^", IXOR, LXOR),

        CMP(Integer.TYPE, "==", LCMP), CMPL(Integer.TYPE, "cmpl", FCMPL, DCMPL), CMPG(Integer.TYPE, "cmpg", FCMPG,
                DCMPG);

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

}
