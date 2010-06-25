define int where $ >=0 && $ <= 255 as byte
define (byte b1, byte b2) as bytes

void System::main([string] args):
    byte b
    bytes bs
    b = 1
    bs = (b1:1,b2:2)
    print str(bs)
    bs = (b1:b,b2:b)
    print str(bs)
