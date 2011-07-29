// Tests that an actor can correctly use the result of a synchronous message.
void System::main([string] args):
    i = this.var()
    out!println(this.self())
    out!println(i)

string System::self():
    return str(1)

string System::var():
    return str(2)
