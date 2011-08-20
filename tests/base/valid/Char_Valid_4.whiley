import whiley.lang.*:*

bool isChar(any x):
    if x is char:
        return true
    else:
        return false

void System::main([string] args):
    this.out.println(str(isChar('c')))
    this.out.println(str(isChar(1)))
    this.out.println(str(isChar([1,2,3])))
