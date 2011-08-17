define bytes as {int8 b1, int8 b2}

bytes f(int8 b):
    return {b1:b,b2:2}

void System::main([string] args):
    b = 1
    bs = f(b)
    out.println(str(bs))
    bs = {b1:b,b2:b}
    out.println(str(bs))
