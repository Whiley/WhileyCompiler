void System::main([string] args):
    i = this.var()
    out!println(this.self())
    out!println(i)

string System::self():
    return str(6)

string System::var():
    return str(5)
