import whiley.lang.*:*

define rlist as real | [int]

int f(rlist l):
    if l is real:
        return 0
    else:
        return |l|

void System::main([string] args):
    this.out.println(str(f(123)))
    this.out.println(str(f(1.23)))
    this.out.println(str(f([1,2,3]))) 

