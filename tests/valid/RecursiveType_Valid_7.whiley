type i16 is (int x) where -32768 <= x && x <= 32767
type string is int[]

constant T_VOID is 3
constant T_BOOLEAN is 4
constant T_CHAR is 5
constant T_FLOAT is 6
constant T_DOUBLE is 7
constant T_BYTE is 8
constant T_SHORT is 9
constant T_INT is 10
constant T_LONG is 11

type primitive_t is (int x) where T_BOOLEAN <= x && x <= T_LONG

type array_t is {jvm_t element}

type class_t is {string[] classes, string pkg}

type ref_t is array_t | class_t
type fun_t is {jvm_t ret, jvm_t[] params}
type jvm_t is primitive_t | ref_t
type Unit is {int op, int offset}
type Branch is {int op, i16 off, int offset}
type VarIndex is {int index, int op, int offset}
type MethodIndex is {int op, string name, class_t owner, int offset, fun_t type}
type FieldIndex is {int op, string name, class_t owner, int offset, jvm_t type}
type ConstIndex is {int index, int op, int offset}
type Bytecode is Unit | VarIndex | Branch | MethodIndex | FieldIndex | ConstIndex

function Unit(int offset, int op) -> Unit:
    return {op: op, offset: offset}

function VarIndex(int offset, int op, int index) -> VarIndex:
    return {index: index, op: op, offset: offset}

function MethodIndex(int offset, int op, class_t owner, string name, fun_t type) -> MethodIndex:
    return {op: op, name: name, owner: owner, offset: offset, type: type}

function FieldIndex(int offset, int op, class_t owner, string name, jvm_t type) -> FieldIndex:
    return {op: op, name: name, owner: owner, offset: offset, type: type}

function ConstIndex(int offset, int op, int index) -> ConstIndex:
    return {index: index, op: op, offset: offset}

function code2toString(Bytecode b) -> string:
    if b is MethodIndex:
        return bytecodeStrings[b.op]
    else:
        return bytecodeStrings[b.op]

constant bytecodeStrings is ["nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2", "iconst_3", "iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0", "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush", "ldc", "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload", "aload", "iload_0", "iload_1", "iload_2", "iload_3", "lload_0", "lload_1", "lload_2", "lload_3", "fload_0", "fload_1", "fload_2", "fload_3", "dload_0", "dload_1", "dload_2", "dload_3", "aload_0", "aload_1", "aload_2", "aload_3", "iaload", "laload", "faload", "daload", "aaload", "baload", "caload", "saload", "istore", "lstore", "fstore", "dstore", "astore", "istore_0", "istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1", "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2", "fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0", "astore_1", "astore_2", "astore_3", "iastore", "lastore", "fastore", "dastore", "aastore", "bastore", "castore", "sastore", "pop", "pop2", "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1", "dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd", "isub", "lsub", "fsub", "dsub", "imul", "lmul", "fmul", "dmul", "idiv", "ldiv", "fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg", "fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr", "iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f", "i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl", "dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle", "if_icmpeq", "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret", "tableswitch", "lookupswitch", "ireturn", "lreturn", "freturn", "dreturn", "areturn", "return", "getstatic", "putstatic", "getfield", "putfield", "invokevirtual", "invokespecial", "invokestatic", "invokeerface", "unused", "new", "newarray", "anewarray", "arraylength", "athrow", "checkcast", "instanceof", "monitorenter", "monitorexit", "wide", "multianewarray", "ifnull", "ifnonnull", "goto_w", "jsr_w", "breakpo", "impdep1", "impdep2"]

public export method test() :
    int[] s1 = code2toString(Unit(0, 1))
    assume s1 == "aconst_null"
    int[] s2 = code2toString(FieldIndex(0, 180, {classes: ["Object"], pkg: "java.lang"}, "field", T_INT))
    assume s2 == "getfield"
