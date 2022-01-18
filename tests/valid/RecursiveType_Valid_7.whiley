type u8 is (int x) where 0 <= x && x <= 255
type i16 is (int x) where -32768 <= x && x <= 32767
type string is int[]

final int T_VOID = 3
final primitive_t T_BOOLEAN = 4
final primitive_t T_CHAR = 5
final primitive_t T_FLOAT = 6
final primitive_t T_DOUBLE = 7
final primitive_t T_BYTE = 8
final primitive_t T_SHORT = 9
final primitive_t T_INT = 10
final primitive_t T_LONG = 11

type primitive_t is (int x) where T_BOOLEAN <= x && x <= T_LONG
type array_t is {jvm_t element}
type class_t is {string[] classes, string pkg}

type ref_t is array_t | class_t
type fun_t is {jvm_t ret, jvm_t[] params}
type jvm_t is primitive_t | ref_t
type Unit is {u8 op, int offset}
type Branch is {u8 op, i16 off, int offset}
type VarIndex is {int index, u8 op, int offset}
type MethodIndex is {u8 op, string name, class_t owner, int offset, fun_t type}
type FieldIndex is {u8 op, string name, class_t owner, int offset, jvm_t type}
type ConstIndex is {int index, u8 op, int offset}
type Bytecode is Unit | VarIndex | Branch | MethodIndex | FieldIndex | ConstIndex

function Unit(int offset, u8 op) -> Unit:
    return {op: op, offset: offset}

function VarIndex(int offset, u8 op, int index) -> VarIndex:
    return {index: index, op: op, offset: offset}

function MethodIndex(int offset, u8 op, class_t owner, string name, fun_t type) -> MethodIndex:
    return {op: op, name: name, owner: owner, offset: offset, type: type}

function FieldIndex(int offset, u8 op, class_t owner, string name, jvm_t type) -> FieldIndex:
    return {op: op, name: name, owner: owner, offset: offset, type: type}

function ConstIndex(int offset, u8 op, int index) -> ConstIndex:
    return {index: index, op: op, offset: offset}

function code2toString(Bytecode b) -> string:
    if b is Unit:
        return bytecodeStrings[b.op]
    else if b is VarIndex:
        return bytecodeStrings[b.op]
    else if b is Branch:
        return bytecodeStrings[b.op]
    else if b is MethodIndex:
        return bytecodeStrings[b.op]
    else if b is FieldIndex:
        return bytecodeStrings[b.op]
    else: // ConstIndex
        return bytecodeStrings[b.op]        

final string[] bytecodeStrings = ["nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2", "iconst_3", "iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0", "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush", "ldc", "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload", "aload", "iload_0", "iload_1", "iload_2", "iload_3", "lload_0", "lload_1", "lload_2", "lload_3", "fload_0", "fload_1", "fload_2", "fload_3", "dload_0", "dload_1", "dload_2", "dload_3", "aload_0", "aload_1", "aload_2", "aload_3", "iaload", "laload", "faload", "daload", "aaload", "baload", "caload", "saload", "istore", "lstore", "fstore", "dstore", "astore", "istore_0", "istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1", "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2", "fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0", "astore_1", "astore_2", "astore_3", "iastore", "lastore", "fastore", "dastore", "aastore", "bastore", "castore", "sastore", "pop", "pop2", "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1", "dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd", "isub", "lsub", "fsub", "dsub", "imul", "lmul", "fmul", "dmul", "idiv", "ldiv", "fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg", "fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr", "iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f", "i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl", "dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle", "if_icmpeq", "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret", "tableswitch", "lookupswitch", "ireturn", "lreturn", "freturn", "dreturn", "areturn", "return", "getstatic", "putstatic", "getfield", "putfield", "invokevirtual", "invokespecial", "invokestatic", "invokeerface", "unused", "new", "newarray", "anewarray", "arraylength", "athrow", "checkcast", "instanceof", "monitorenter", "monitorexit", "wide", "multianewarray", "ifnull", "ifnonnull", "goto_w", "jsr_w", "breakpo", "impdep1", "impdep2"]

public export method test() :
    int[] s1 = code2toString(Unit(0, 1))
    assume s1 == "aconst_null"
    int[] s2 = code2toString(FieldIndex(0, 180, {classes: ["Object"], pkg: "java.lang"}, "field", T_INT))
    assume s2 == "getfield"
