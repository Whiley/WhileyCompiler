import whiley.lang.*

type pos is (int p) where p > 0

type neg is (int n) where n < 0

type expr is pos | neg

function f(expr e) -> bool:
    if e is pos:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f(-1) == false
    assume f(1) == true
    assume f(1234) == true
