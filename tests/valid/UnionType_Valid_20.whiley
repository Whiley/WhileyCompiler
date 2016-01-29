type i16 is (int x) where -32768 <= x && x <= 32767
type immStoreCode is (int x) where 0 <= x && x <= 2
type storeCode is (int x) where  0 <= x &&x <= 5

type STORE is {int index, storeCode op}

type branchCode is (int x) where 6 <= x && x <= 8

type BRANCH is {branchCode op, i16 offset}

type byteCode is STORE | BRANCH

function f(byteCode b) -> byteCode:
    return b

public export method test() :
    STORE b = {index: 1, op: 0}
    assume f(b) == {index: 1, op: 0}
