import println from whiley.lang.System

define imsg as int|{int op}|{string msg}

string getMessage(imsg m):
    if m is {string msg}:
        return m.msg
    else if m is {int op}:
        return Any.toString(m.op)
    else:
        return Any.toString(m)

void ::main(System.Console sys):
    sys.out.println(getMessage({msg:"HELLO WORLD"}))
    sys.out.println(getMessage(1))
    sys.out.println(getMessage({op:123}))
