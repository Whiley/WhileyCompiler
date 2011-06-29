bool isChar(any x):
    if x is char:
        return true
    else:
        return false

void System::main([string] args):
    out.println(str(isChar('c')))
    out.println(str(isChar(1)))
    out.println(str(isChar([1,2,3])))
