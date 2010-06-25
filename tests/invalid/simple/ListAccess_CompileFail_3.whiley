define int|[int] as intList
define (int mode, intList data) as tup

void System::main([string] args):
    [tup] tups = [(mode:0,data:1),(mode:1,data:[1,2,3])]
    [(int mode, int data)] btups
    tups[0].data = 1
    btups = tups // NOT OK
    print str(tups)

