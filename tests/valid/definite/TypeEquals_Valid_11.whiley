define imsg as int|{string msg}

string getMessage(imsg m):
    if m ~= {string msg}:
        return m.msg
    else:
        return str(m)

void System::main([string] args):
    out->println(getMessage(){msg:"HELLO WORLD"})
    out->println(getMessage(1))
