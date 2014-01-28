import whiley.lang.System

type Attribute is {string name, ...}

type CodeAttr is {int maxLocals, int maxStack, string name, [byte] data}

function match(Attribute attr) => bool:
    if attr is CodeAttr:
        return true
    else:
        return false

method main(System.Console sys) => void:
    Attribute r = {name: "Hello"}
    sys.out.println("MATCHED: " ++ match(r))
    r = {maxLocals: 0, maxStack: 0, name: "Code", data: []}
    sys.out.println("MATCHED: " ++ match(r))
