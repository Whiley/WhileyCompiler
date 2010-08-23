define imsg as int|(int op)|(string msg)

string getMessage(imsg m):
    if m ~= (string msg):
        return m.msg
    else if m ~= (int op):
        return str(m.op)
    else:
        return str(m)

void System::main([string] args):
    print getMessage((msg:"HELLO WORLD"))
    print getMessage(1)
    print getMessage((op:123))
