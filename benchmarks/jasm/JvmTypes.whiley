// =========== JVM Types ==============

define T_BOOLEAN as 4
define T_CHAR as 5
define T_FLOAT as 6
define T_DOUBLE as 7
define T_BYTE as 8
define T_SHORT as 9
define T_INT as 10
define T_LONG as 11

define primitive_t as { T_BOOLEAN, T_CHAR, T_FLOAT, T_DOUBLE, T_BYTE, T_SHORT, T_INT, T_LONG }
define class_t as [string]
define jvm_t as primitive_t | class_t

int slotSize(primitive_t type) ensures $==1 || $==2:
    if(type == T_DOUBLE || type == T_LONG):
        return 2
    else:
        return 1

// =========== Opcode Types ==============

define intCodes as { 
    ICONST_M1, 
    ICONST_0, 
    ICONST_1, 
    ICONST_2, 
    ICONST_3, 
    ICONST_4, 
    ICONST_5, 
    ILOAD, 
    ILOAD_0, 
    ILOAD_1, 
    ILOAD_2, 
    ILOAD_3, 
    ISTORE, 
    ISTORE_0, 
    ISTORE_1, 
    ISTORE_2, 
    ISTORE_3, 
    IADD, 
    IMUL, 
    IDIV, 
    IREM, 
    ISHL, 
    ISHR, 
    IUSHR, 
    IAND, 
    IOR, 
    IXOR
}

define longCodes as { 
    LCONST_0, 
    LCONST_1, 
    LLOAD, 
    LLOAD_0, 
    LLOAD_1, 
    LLOAD_2, 
    LLOAD_3, 
    LSTORE, 
    LSTORE_0, 
    LSTORE_1, 
    LSTORE_2, 
    LSTORE_3, 
    LADD, 
    LMUL, 
    LDIV, 
    LREM, 
    LSHL, 
    LSHR, 
    LUSHR, 
    LAND, 
    LOR, 
    LXOR 
}

define floatCodes as { 
    FCONST_0, 
    FCONST_1, 
    FLOAD, 
    FLOAD_0, 
    FLOAD_1, 
    FLOAD_2, 
    FLOAD_3, 
    FSTORE, 
    FSTORE_0, 
    FSTORE_1, 
    FSTORE_2, 
    FSTORE_3, 
    FADD, 
    FMUL, 
    FDIV, 
    FREM
}

define doubleCodes as { 
    DCONST_0, 
    DCONST_1, 
    DLOAD, 
    DLOAD_0, 
    DLOAD_1, 
    DLOAD_2, 
    DLOAD_3, 
    DSTORE, 
    DSTORE_0, 
    DSTORE_1, 
    DSTORE_2, 
    DSTORE_3, 
    DADD, 
    DMUL, 
    DDIV, 
    DREM
}

define typedCodes as intCodes ∪ longCodes ∪ floatCodes ∪ doubleCodes

// This method returns the operand type for bytecodes which
//  manipulate primitive types.
primitive_t jvmType(typedCodes opcode):
    if(opcode in intCodes):
        return T_INT
    else if(opcode in longCodes):
        return T_LONG
    else if(opcode in floatCodes):
        return T_FLOAT
    else if(opcode in doubleCodes):
        return T_DOUBLE
    else:
        return T_LONG
