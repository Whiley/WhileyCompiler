define imsg as int|{int op}|{string msg}

string getMessage(imsg m):
    if m is {string msg}:
        return m.msg
    else if m is {int op}:
        return str(m.op)
    else:
        return str(m)

void System::main([string] args):
    out.println(getMessage({msg:"HELLO WORLD"}))
    out.println(getMessage(1))
    out.println(getMessage({op:123}))
