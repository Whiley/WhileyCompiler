define R1 as { real x }

[real] f([int] xs):
    return ([real]) xs

void System::main([string] args):
    this.out.println(str(f([1,2,3])))
    
