define byte as int where $ >=0 && $ <= 255
define bytes as { byte b1, byte b2 }

bytes f(int a) requires a > 0 && a < 10:
    bs = {b1:a,b2:a+1}
    return bs

void System::main([string] args):
    out.println(str(f(1)))
    out.println(str(f(2)))
    out.println(str(f(9)))
