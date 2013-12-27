import println from whiley.lang.System

define Attribute as {string name,...}
define CodeAttr as {int maxLocals,int maxStack,string name,[byte] data}

bool match(Attribute attr):
    if attr is CodeAttr:
        return true
    else:
        return false

void ::main(System.Console sys):
    r = { name: "Hello" }
    sys.out.println("MATCHED: " + match(r))
    r = { name: "Code", maxLocals: 0, maxStack: 0, data: []}
    sys.out.println("MATCHED: " + match(r))

