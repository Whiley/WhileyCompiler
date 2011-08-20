import whiley.lang.*:*

{real} f([real] x):
    return x

void System::main([string] args):
    x = f([2.2,3.3])
    this.out.println(str(x))