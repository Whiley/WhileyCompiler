import whiley.lang.*:*


define immStoreCode as { 0,1,2 }
define storeCode as { 3,4,5 } ∪ immStoreCode
define STORE as {storeCode op, int index}

define branchCode as { 6,7,8 }
define BRANCH as {branchCode op, int16 offset}

define byteCode as STORE | BRANCH

string f(byteCode b):
    return str(b)

void ::main(System sys,[string] args):
    b = {op:0,index:1}
    sys.out.println(f(b))

