import whiley.lang.*:*

define sr3nat as int

void System::main([string] args):
    x = [1]
    x[0] = 1
    this.out.println(str(x))
    
