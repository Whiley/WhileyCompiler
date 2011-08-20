import whiley.lang.*:*

define R1 as { int x }
define R2 as { int x, int y }

int f(bool flag, [int] list):
    r = 0
    if flag:        
        for pos in list:
            r = r + pos
    else:
        for pos in list:
            r = r - pos
    return r

void System::main([string] args):
    r1 = f(true,[1,2,3,4,5,6,7,8,9,10])
    r2 = f(false,[1,2,3,4,5,6,7,8,9,10])
    this.out.println(str(r1))
    this.out.println(str(r2))