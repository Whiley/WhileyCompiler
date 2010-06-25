
define { 0,1,2 } as immStoreCode
define { 3,4,5 } ∪ immStoreCode as storeCode
define (storeCode op, byte index) as STORE

define { 6,7,8 } as branchCode
define (branchCode op, int16 offset) as BRANCH

define STORE | BRANCH as byteCode

void System::main([string] args):
    byteCode b = (op:0,index:1)
    print str(b)

