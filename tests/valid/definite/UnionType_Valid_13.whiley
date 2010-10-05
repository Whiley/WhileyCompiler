
define immStoreCode as { 0,1,2 }
define storeCode as { 3,4,5 } ∪ immStoreCode
define STORE as {storeCode op, byte index}

define branchCode as { 6,7,8 }
define BRANCH as {branchCode op, int16 offset}

define byteCode as STORE | BRANCH

void f(byteCode b):
    out->println(str(b))

void System::main([string] args):
    b = {op:0,index:1}
    f(b)

