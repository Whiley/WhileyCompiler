define btr1tup as {bool flag, int code}

void System::main([string] args):
    btr1tup x = {flag:true,code:0}
    print str(x)
    x.flag = false
    print str(x)
