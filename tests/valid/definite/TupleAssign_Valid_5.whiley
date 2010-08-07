define byte as int requires $ >=0 && $ <= 255
define bytes as (byte b1, byte b2)

bytes f(int a) requires a > 0 && a < 10:
    bytes bs
    bs = (b1:a,b2:a+1)
    return bs

void System::main([string] args):
    print str(f(1))
    print str(f(2))
    print str(f(9))
