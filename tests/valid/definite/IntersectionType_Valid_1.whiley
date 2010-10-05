// this is a comment!
define {-2,-1,0,1,2,3,4,5,6} as itr1xs
define itr1nat as {x | x in itr1xs, x > 0}
define lten as {x | x in itr1xs, x < 10}
define itr1nat ∩ lten as bounded

void System::main([string] args):
    bounded x
    x = 1
    out->println(str(x))
    x = 5
    out->println(str(x))

