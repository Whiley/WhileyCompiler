import whiley.lang.*

function run(int n, int x) -> bool:
    bool solution = true
    for i in 0 .. n:
        if i == x:
            solution = false
            break
    return solution

method main(System.Console sys) -> void:
    bool b1 = run(10, 4)
    sys.out.println_s("b1=" ++ Any.toString(b1))
    bool b2 = run(10, -1)
    sys.out.println_s("b2=" ++ Any.toString(b2))
    bool b3 = run(10, 11)
    sys.out.println_s("b3=" ++ Any.toString(b3))
