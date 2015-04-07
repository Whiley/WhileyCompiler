import whiley.lang.*

type fr8nat is (int x) where x > 0

type fr8neg is (int x) where x < 0

function f(fr8nat y) -> bool:
    return true

function f(fr8neg x) -> bool:
    return false

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(-1))    
