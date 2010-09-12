define btr2tup as (bool flag, int code)

void System::main([string] args):
    btr2tup x = (flag:true,code:0)
    if(x.flag):
        print "GOT HERE"
