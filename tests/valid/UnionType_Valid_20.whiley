import whiley.lang.System

constant immStoreCode is {0, 1, 2}

constant storeCode is {3, 4, 5} + immStoreCode

type STORE is {int index, storeCode op}

constant branchCode is {6, 7, 8}

type BRANCH is {branchCode op, Int.i16 offset}

type byteCode is STORE | BRANCH

function f(byteCode b) => string:
    return Any.toString(b)

method main(System.Console sys) => void:
    STORE b = {index: 1, op: 0}
    sys.out.println(f(b))
