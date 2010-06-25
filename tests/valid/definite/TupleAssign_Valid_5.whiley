define int where $ >=0 && $ <= 255 as byte
define (byte b1, byte b2) as bytes

bytes f(int a) requires a > 0 && a < 10:
    bytes bs
    bs = (b1:a,b2:a+1)
    return bs

void System::main([string] args):
    print str(f(1))
    print str(f(2))
    print str(f(9))
