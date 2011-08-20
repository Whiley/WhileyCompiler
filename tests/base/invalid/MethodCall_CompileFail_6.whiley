import whiley.lang.*:*

define wmccf6tup as {int x, int y}

wmccf6tup f(System x, int y):
    return {x:1, y:x.get()}

int System::get():
    return 1

void System::main([string] args):
    this.out.println(str(f(this),1))
