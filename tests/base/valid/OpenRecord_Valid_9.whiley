import * from whiley.lang.*

define Void as 3
define Boolean as 4
define Char as 5
define Float as 6
define Double as 7
define Byte as 8
define Short as 9
define Int as 10
define Long as 11

define Primitive as { Boolean, Char, Float, Double, Byte, Short, Int, Long }
define Array as { Any element }
define Class as { string pkg, [string] classes }
define Ref as Array | Class
define Fun as { Any ret, [Any] params }
define Any as Primitive | Ref

define Unit as { int offset, int op }
define Branch as { int offset, int op, Type.int16 offset }

define VarIndex as { int offset, int op, int index }
define MethodIndex as { int offset, int op, Class owner, string name, Fun type }
define FieldIndex as { int offset, int op, Class owner, string name, Any type }

define Bytecode as Unit | VarIndex | Branch | MethodIndex | FieldIndex


define Attribute as {
 string name,
 ...
}

define CodeAttribute as {
 string name,
 [Bytecode] data
}

null|int codeLength([Attribute] attributes):
    for a in attributes:
        if a is CodeAttribute:
            return |a.data|
    return null

public void ::main(System.Console sys):
    attrs = [{name: "Blah"},{name: "Code",data: [11101b]}]
    sys.out.println(codeLength(attrs))
    attrs = [{name: "Blah"},{name: "Other",index: 1}]
    sys.out.println(codeLength(attrs))
