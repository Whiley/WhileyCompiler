define byte as int where $ >=0 && $ <= 255
define bytes as {byte b1, byte b2}

bytes f(byte b):
    return {b1:b,b2:2}

void System::main([string] args):
    b = 1
    bs = f(b)
    out.println(str(bs))
    bs = {b1:b,b2:b}
    out.println(str(bs))
