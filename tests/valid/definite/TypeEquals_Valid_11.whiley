define imsg as int|{string msg}

string getMessage(imsg m):
    if m ~= {string msg}:
        return m.msg
    else:
        return str(m)

void System::main([string] args):
    print getMessage({msg:"HELLO WORLD"})
    print getMessage(1)
