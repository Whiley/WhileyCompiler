[int] f([real] e):
    if e ~= [int]:
        return e
    else:
        return [1,2,3]

void System::main([string] args):
    out.println(str(f([1,2,3,4,5,6,7])))
    out.println(str(f([])))
    out.println(str(f([1,2,2.01])))
    out.println(str(f([1.23,2,2.01])))
