string iof(int i):
    return "" + ('a' + i) + ('1' + i)

void System::main([string] args):
    out.println(str(iof(0)))
    out.println(str(iof(1)))
    out.println(str(iof(2)))
    out.println(str(iof(3)))
    out.println(str(iof(4)))
