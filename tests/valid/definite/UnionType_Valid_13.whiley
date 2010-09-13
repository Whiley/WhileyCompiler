
define immStoreCode as { 0,1,2 }
define storeCode as { 3,4,5 } ∪ immStoreCode
define STORE as {storeCode op, byte index}

define branchCode as { 6,7,8 }
define BRANCH as {branchCode op, int16 offset}

define byteCode as STORE | BRANCH

void System::main([string] args):
    byteCode b = {op:0,index:1}
    print str(b)

