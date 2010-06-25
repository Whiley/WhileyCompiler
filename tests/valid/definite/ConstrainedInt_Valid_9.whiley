// this is a comment!
define { 1,3,5 } as odd
define { 2,4,6 } as even
define odd ∪ even as oddeven

even f(oddeven x):
    if(x ∈ odd):
        return 2
    return x
    
void System::main([string] args):
    oddeven y = 1
    y = f(1)
    print str(y)
