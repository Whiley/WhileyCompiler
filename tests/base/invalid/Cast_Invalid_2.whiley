define R1 as { real x }

[int] f([real] xs):
    return ([real]) xs

void System::main([string] args):
    out.println(str(f([1.0,2.0,3.0])))
    
