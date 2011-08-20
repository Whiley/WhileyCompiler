import whiley.lang.*:*

int f([int] xs) requires no { x in xs | x < 0}:
    return |xs|

void System::main([string] args):
    right = [-1,0,1]
    // now, fool constant propagation
    if(|args| > 1):
        left = [2,3,4]
    else:
        left = [1,2,3]
    r = f(left + right)
    debug str(r)
