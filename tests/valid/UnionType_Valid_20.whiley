type i16 is (int x) where -32768 <= x && x <= 32767
type immStoreCode is (int x) where x in {0, 1, 2}
type storeCode is (int x) where x in {0, 1, 2, 3, 4, 5}

type STORE is {int index, storeCode op}

type branchCode is (int x) where x in {6, 7, 8}

type BRANCH is {branchCode op, i16 offset}

type byteCode is STORE | BRANCH

function f(byteCode b) -> byteCode:
    return b

public export method test() -> void:
    STORE b = {index: 1, op: 0}
    assume f(b) == {index: 1, op: 0}
