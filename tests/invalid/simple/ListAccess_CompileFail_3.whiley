define intList as int|[int]
define tup as (int mode, intList data)

void System::main([string] args):
    [tup] tups = [(mode:0,data:1),(mode:1,data:[1,2,3])]
    [(int mode, int data)] btups
    tups[0].data = 1
    btups = tups // NOT OK
    print str(tups)

