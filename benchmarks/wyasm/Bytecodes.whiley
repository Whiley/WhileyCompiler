import whiley.lang.Types

// =========== UNIT Bytecodes ==============
define { NOP, RET } as unitCode
define (unitCode op) as UNIT

// =========== BINOP Bytecodes ==============

define { IADD, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR, LADD, LMUL, LDIV, LREM, LSHL, LSHR, LUSHR, LAND, LOR, LXOR, FADD, FMUL, FDIV, FREM, DADD, DMUL, DDIV, DREM } as binOpCode
define (binOpCode op) as BINOP

// =========== STORE Bytecodes ==============

define { ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3 } as immStoreCode
define { ISTORE, LSTORE, FSTORE, DSTORE } ∪ immStoreCode as storeCode
define (storeCode op, byte index) as STORE

// =========== LOAD Bytecodes ==============

define { ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3 } as immLoadCode
define { ILOAD, LLOAD, FLOAD, DLOAD } as loadCode
define (loadCode op, byte index) as LOAD

// =========== BRANCH Bytecodes ==============
define { GOTO, IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE } as branchCode
define (branchCode op, int16 offset) as BRANCH

// =========== All Bytecodes ==============
define UNIT | STORE | LOAD | BRANCH as byteCode

// =========== Translate Bytecodes ==============
[byte] translate(UNIT unit):
    return [unit.op] // easy!

[byte] translate(BINOP binop):
    return [binop.op] // easy!

// Translate store bytecodes
[byte] translate(STORE store):
    if(store.op in immStoreCode):
        return [store.op]
    else:
        return [store.op,store.index]

// Translate load bytecodes
[byte] translate(LOAD load):
    if(load.op in immLoadCode):
        return [load.op]
    else:
        return [load.op,load.index]
