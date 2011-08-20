import whiley.lang.*:*

void f([real] ls):
    this.out.println(str(ls))

void System::main([string] args):
    f([1,2,3,[]])
