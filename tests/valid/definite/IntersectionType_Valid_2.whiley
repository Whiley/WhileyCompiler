// this is a comment!
define {1.1231,2.3,234.234,3.4234,-1.123,0.923} as itr2xs

define {x | x in itr2xs, x > 0.0} as itr2nat
define {x | x in itr2xs, x < 10.0} as lten
define itr2nat ∩ lten as bounded

void System::main([string] args):
    bounded x
    x = 1.1231
    print str(x)
    x = 0.923 
    print str(x)

