import whiley.lang.*

type immStoreCode is (int x) where x in {0, 1, 2}

type storeCode is (int x) where x in {0, 1, 2, 3, 4, 5}

type STORE is {int index, storeCode op}

type branchCode is (int x) where x in {6, 7, 8}

type BRANCH is {branchCode op, Int.i16 offset}

type byteCode is STORE | BRANCH

function f(byteCode b) -> byteCode:
    return b

method main(System.Console sys) -> void:
    STORE b = {index: 1, op: 0}
    sys.out.println(f(b))
