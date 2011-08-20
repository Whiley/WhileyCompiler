import whiley.lang.*:*

string f({real} ls):
    return str(ls)

void System::main([string] args):
    this.out.println(f({1,2,3}))
