string f(int|null x):
    if x ~= null:
        return "GOT NULL"
    else:
        return "GOT INT"

void System::main([string] args):
    x = null
    out<->println(f(x))
    out<->println(f(1))
