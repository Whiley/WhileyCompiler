define rlist as real | [int]

int f(rlist l):
    if l is real:
        return 0
    else:
        return |l|

void System::main([string] args):
    out.println(str(f(123)))
    out.println(str(f(1.23)))
    out.println(str(f([1,2,3]))) 

