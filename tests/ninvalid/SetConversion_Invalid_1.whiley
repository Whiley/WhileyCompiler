import * from whiley.lang.*

function f({real} ls) => void:
    sys.out.println(Any.toString(ls))

method main(System.Console sys) => void:
    f({1, 2, 3, {}})
