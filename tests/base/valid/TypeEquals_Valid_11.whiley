define imsg as int|{string msg}

string getMessage(imsg m):
    if m is {string msg}:
        return m.msg
    else:
        return str(m)

void System::main([string] args):
    this.out.println(getMessage({msg:"HELLO WORLD"}))
    this.out.println(getMessage(1))
