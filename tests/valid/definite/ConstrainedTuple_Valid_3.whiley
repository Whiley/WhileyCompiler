define (byte op, byte index) as btup

[byte] f(btup b):        
    return [b.op,b.index]

void System::main([string] args):
    print str(f((op:1,index:2)))
