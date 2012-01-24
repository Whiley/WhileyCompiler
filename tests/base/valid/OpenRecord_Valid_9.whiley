import * from whiley.lang.*

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
