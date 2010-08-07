void f([int] xs) requires xs != []:
    print str(xs)

void System::main([string] args):
    f([1,4])
    f([])
