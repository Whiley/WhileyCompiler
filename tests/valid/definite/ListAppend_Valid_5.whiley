int f([int] xs) requires no { x in xs | x < 0}:
    return |xs|

void System::main([string] args):
    left = [1,2,3]
    right = [5,6,7]
    r = f(left + right)
    print str(r)
