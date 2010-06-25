// =========== JVM Types ==============

define 4 as T_BOOLEAN
define 5 as T_CHAR
define 6 as T_FLOAT
define 7 as T_DOUBLE
define 8 as T_BYTE
define 9 as T_SHORT
define 10 as T_INT
define 11 as T_LONG

define { T_BOOLEAN, T_CHAR, T_FLOAT, T_DOUBLE, T_BYTE, T_SHORT, T_INT, T_LONG } as jvmType

int slotSize(jvmType type) ensures $==1 || $==2:
    if(type == T_DOUBLE || type == T_LONG):
        return 2
    else:
        return 1

// =========== Opcode Types ==============

define { ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, ILOAD, ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3, ISTORE, ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3, IADD, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR } as intCodes

define { LCONST_0, LCONST_1, LLOAD, LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3, LSTORE, LSTORE_0, LSTORE_1, LSTORE_2, LSTORE_3, LADD, LMUL, LDIV, LREM, LSHL, LSHR, LUSHR, LAND, LOR, LXOR } as longCodes

define { FCONST_0, FCONST_1, FLOAD, FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3, FSTORE, FSTORE_0, FSTORE_1, FSTORE_2, FSTORE_3, FADD, FMUL, FDIV, FREM } as floatCodes

define { DCONST_0, DCONST_1, DLOAD, DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3, DSTORE, DSTORE_0, DSTORE_1, DSTORE_2, DSTORE_3, DADD, DMUL, DDIV, DREM } as doubleCodes

define intCodes ∪ longCodes ∪ floatCodes ∪ doubleCodes as typedCodes

// This method returns the operand type for bytecodes which
//  manipulate primitive types.
jvmType jvmType(typedCodes opcode):
    if(opcode ∈ intCodes):
        return T_INT
    else if(opcode ∈ longCodes):
        return T_LONG
    else if(opcode ∈ floatCodes):
        return T_FLOAT
    else if(opcode ∈ doubleCodes):
        return T_DOUBLE
    else:
        return T_LONG
