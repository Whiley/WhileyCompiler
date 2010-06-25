define int where $ >= 0 as a_nat
define int where 2*$ >= $ as b_nat

b_nat f(a_nat x):
    if x == 0:
        return 1
    else:
        return f(x-1)

void System::main([string] args):
    int x = |args|
    
    x = f(x)
    
    print str(x)
