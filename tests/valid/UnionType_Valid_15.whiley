import println from whiley.lang.System

define immStoreCode as { 0,1,2 }
define storeCode as { 3,4,5 } ∪ immStoreCode
define STORE as {storeCode op, int index}

define branchCode as { 6,7,8 }
define BRANCH as {branchCode op, Int.i16 offset}

define byteCode as STORE | BRANCH

string f(byteCode b):
    return Any.toString(b)

void ::main(System.Console sys):
    b = {op:0,index:1}
    sys.out.println(f(b))

