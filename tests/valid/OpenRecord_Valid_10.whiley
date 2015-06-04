

type string is [int]
type Attribute is {string name, ...}

type CodeAttr is {int maxLocals, int maxStack, string name, [byte] data}

function match(Attribute attr) -> bool:
    if attr is CodeAttr:
        return true
    else:
        return false

public export method test() -> void:
    Attribute r = {name: "Hello"}
    assume match(r) == false
    r = {maxLocals: 0, maxStack: 0, name: "Code", data: []}
    assume match(r) == true
