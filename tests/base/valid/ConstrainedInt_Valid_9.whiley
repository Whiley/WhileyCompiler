import whiley.lang.*:*

// this is a comment!
define odd as { 1,3,5 }
define even as { 2,4,6 }
define oddeven as odd ∪ even

even f(oddeven x):
    if(x ∈ odd):
        return 2
    return x
    
void System::main([string] args):
    y = 1
    y = f(1)
    this.out.println(str(y))
