define fr8nat as int where $ > 0
define fr8neg as int where $ < 0

string f(fr8nat y):
    return "F(NAT)"

string f(fr8neg x):
    return "F(NEG)"

void System::main([string] args):
    this.out.println(f(-1))
    this.out.println(f(1))
