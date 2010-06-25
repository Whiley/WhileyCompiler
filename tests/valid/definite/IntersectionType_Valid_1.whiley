// this is a comment!
define {-2,-1,0,1,2,3,4,5,6} as itr1xs
define {x | x in itr1xs, x > 0} as itr1nat
define {x | x in itr1xs, x < 10} as lten
define itr1nat ∩ lten as bounded

void System::main([string] args):
    bounded x
    x = 1
    print str(x)
    x = 5
    print str(x)

