void f([int] xs) where xs != [false]:
    print str(xs)

void System::main([string] args):
    f([1,4])
    f([])
