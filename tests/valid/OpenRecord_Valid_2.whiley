import println from whiley.lang.System

define Short as 3
define Int as 4

define Primitive as { Short, Int }
define Class as { string pkg, [string] classes }
define Any as Primitive | Class

define Unit as { int offset, int op }
define FieldIndex as { int offset, int op, Class owner, string name, Any type }

define Bytecode as Unit | FieldIndex


define Attribute as {
 string name,
 ...
}

define CodeAttribute as {
 string name,
 [Bytecode] data
}

null|int codeLength(Attribute a):
    if a is CodeAttribute:
        return |a.data|
    return null

public void ::main(System.Console sys):
    attr = {name: "Code",data: [{offset: 1, op: 2}]}
    sys.out.println(codeLength(attr))
    attr = {name: "Blah"}
    sys.out.println(codeLength(attr))
