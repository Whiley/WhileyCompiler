

type string is int[]
type Attribute is {string name, ...}

type CodeAttr is {int maxLocals, int maxStack, string name, byte[] data}

function match(Attribute attr) -> bool:
    if attr is CodeAttr:
        return true
    else:
        return false

public export method test() :
    Attribute r = {name: "Hello"}
    assume match(r) == false
    r = {maxLocals: 0, maxStack: 0, name: "Code", data: [0b;0]}
    assume match(r) == true
