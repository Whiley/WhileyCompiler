define {1,2,3,4} as fcode
define {1,2} as tcode

void g(fcode f):
    print str(f)

void System::main([string] args):
    tcode x = 1
    g(x)
