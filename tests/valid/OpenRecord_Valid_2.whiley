import whiley.lang.System

constant Short is 3

constant Int is 4

constant Primitive is {Short, Int}

type Class is {[string] classes, string pkg}

type Any is Primitive | Class

type Unit is {int op, int offset}

type FieldIndex is {int op, string name, Class owner, int offset, Any type}

type Bytecode is Unit | FieldIndex

type Attribute is {string name, ...}

type CodeAttribute is {string name, [Bytecode] data}

function codeLength(Attribute a) => null | int:
    if a is CodeAttribute:
        return |a.data|
    return null

public method main(System.Console sys) => void:
    Attribute attr = {name: "Code", data: [{op: 2, offset: 1}]}
    sys.out.println(codeLength(attr))
    attr = {name: "Blah"}
    sys.out.println(codeLength(attr))
