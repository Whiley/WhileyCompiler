int f([int] xs) where no { x in xs | x < 0}:
    return |xs|

void System::main([string] args):
    [int] left = [1,2,3]
    [int] right = [-1,0,1]
    int r = f(left + right)
    print str(r)
