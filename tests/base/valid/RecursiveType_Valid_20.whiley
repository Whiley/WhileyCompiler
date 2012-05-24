import println from whiley.lang.System

// ===========================================
// Bytecode Structures
// ===========================================

define T_VOID as 3
define T_BOOLEAN as 4
define T_CHAR as 5
define T_FLOAT as 6
define T_DOUBLE as 7
define T_BYTE as 8
define T_SHORT as 9
define T_INT as 10
define T_LONG as 11

define primitive_t as { T_BOOLEAN, T_CHAR, T_FLOAT, T_DOUBLE, T_BYTE, T_SHORT, T_INT, T_LONG }
define array_t as { jvm_t element }
define class_t as { string pkg, [string] classes }
define ref_t as array_t | class_t
define fun_t as { jvm_t ret, [jvm_t] params }
define jvm_t as primitive_t | ref_t

define Unit as { int offset, int op }
define Branch as { int offset, int op, Int.i16 offset }

define VarIndex as { int offset, int op, int index }
define MethodIndex as { int offset, int op, class_t owner, string name, fun_t type }
define FieldIndex as { int offset, int op, class_t owner, string name, jvm_t type }
define ConstIndex as { int offset, int op, int index }

define Bytecode as Unit | VarIndex | Branch | MethodIndex | FieldIndex | ConstIndex

// ===========================================
// Bytecode Constructors
// ===========================================

Unit Unit(int offset, int op):
    return {offset: offset, op: op}

VarIndex VarIndex(int offset, int op, int index):
    return {offset: offset, op: op, index: index}

MethodIndex MethodIndex(int offset, int op, class_t owner, string name, fun_t type):
    return {offset: offset, op: op, owner: owner, name: name, type: type}

FieldIndex FieldIndex(int offset, int op, class_t owner, string name, jvm_t type):
    return {offset: offset, op: op, owner: owner, name: name, type: type}

ConstIndex ConstIndex(int offset, int op, int index):
    return {offset: offset, op: op, index: index}

// ===========================================
// Bytecode to String Conversion
// ===========================================

string code2toString(Bytecode b):
    if b is MethodIndex:
        return bytecodeStrings[b.op]        
    else:
        return bytecodeStrings[b.op]

define bytecodeStrings as [
    "nop",
    "aconst_null",
    "iconst_m1",
    "iconst_0",
    "iconst_1",
    "iconst_2",
    "iconst_3",
    "iconst_4",
    "iconst_5",
    "lconst_0",
    "lconst_1",
    "fconst_0",
    "fconst_1",
    "fconst_2",
    "dconst_0",
    "dconst_1",
    "bipush",
    "sipush",
    "ldc",
    "ldc_w",
    "ldc2_w",
    "iload",
    "lload",
    "fload",
    "dload",
    "aload",
    "iload_0",
    "iload_1",
    "iload_2",
    "iload_3",
    "lload_0",
    "lload_1",
    "lload_2",
    "lload_3",
    "fload_0",
    "fload_1",
    "fload_2",
    "fload_3",
    "dload_0",
    "dload_1",
    "dload_2",
    "dload_3",
    "aload_0",
    "aload_1",
    "aload_2",
    "aload_3",
    "iaload",
    "laload",
    "faload",
    "daload",
    "aaload",
    "baload",
    "caload",
    "saload",
    "istore",
    "lstore",
    "fstore",
    "dstore",
    "astore",
    "istore_0",
    "istore_1",
    "istore_2",
    "istore_3",
    "lstore_0",
    "lstore_1",
    "lstore_2",
    "lstore_3",
    "fstore_0",
    "fstore_1",
    "fstore_2",
    "fstore_3",
    "dstore_0",
    "dstore_1",
    "dstore_2",
    "dstore_3",
    "astore_0",
    "astore_1",
    "astore_2",
    "astore_3",
    "iastore",
    "lastore",
    "fastore",
    "dastore",
    "aastore",
    "bastore",
    "castore",
    "sastore",
    "pop",
    "pop2",
    "dup",
    "dup_x1",
    "dup_x2",
    "dup2",
    "dup2_x1",
    "dup2_x2",
    "swap",
    "iadd",
    "ladd",
    "fadd",
    "dadd",
    "isub",
    "lsub",
    "fsub",
    "dsub",
    "imul",
    "lmul",
    "fmul",
    "dmul",
    "idiv",
    "ldiv",
    "fdiv",
    "ddiv",
    "irem",
    "lrem",
    "frem",
    "drem",
    "ineg",
    "lneg",
    "fneg",
    "dneg",
    "ishl",
    "lshl",
    "ishr",
    "lshr",
    "iushr",
    "lushr",
    "iand",
    "land",
    "ior",
    "lor",
    "ixor",
    "lxor",
    "iinc",
    "i2l",
    "i2f",
    "i2d",
    "l2i",
    "l2f",
    "l2d",
    "f2i",
    "f2l",
    "f2d",
    "d2i",
    "d2l",
    "d2f",
    "i2b",
    "i2c",
    "i2s",
    "lcmp",
    "fcmpl",
    "fcmpg",
    "dcmpl",
    "dcmpg",
    "ifeq",
    "ifne",
    "iflt",
    "ifge",
    "ifgt",
    "ifle",
    "if_icmpeq",
    "if_icmpne",
    "if_icmplt",
    "if_icmpge",
    "if_icmpgt",
    "if_icmple",
    "if_acmpeq",
    "if_acmpne",
    "goto",
    "jsr",
    "ret",
    "tableswitch",
    "lookupswitch",
    "ireturn",
    "lreturn",
    "freturn",
    "dreturn",
    "areturn",
    "return",
    "getstatic",
    "putstatic",
    "getfield",
    "putfield",
    "invokevirtual",
    "invokespecial",
    "invokestatic",
    "invokeerface",
    "unused",
    "new",
    "newarray",
    "anewarray",
    "arraylength",
    "athrow",
    "checkcast",
    "instanceof",
    "monitorenter",
    "monitorexit",
    "wide",
    "multianewarray",
    "ifnull",
    "ifnonnull",
    "goto_w",
    "jsr_w",
    "breakpo",
    "impdep1",
    "impdep2"
]

void ::main(System.Console sys):
    s1 = code2toString(Unit(0,1))
    sys.out.println(s1)
    s2 = code2toString(FieldIndex(0,180,{ pkg: "java.lang", classes: ["Object"]},"field",T_INT))
    sys.out.println(s2)
