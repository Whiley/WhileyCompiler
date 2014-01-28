import whiley.lang.System

function run(int n, int x) => bool:
    solution = true
    for i in 0 .. n:
        if i == x:
            solution = false
            break
    return solution

method main(System.Console sys) => void:
    b1 = run(10, 4)
    sys.out.println("b1=" ++ Any.toString(b1))
    b2 = run(10, -1)
    sys.out.println("b2=" ++ Any.toString(b2))
    b3 = run(10, 11)
    sys.out.println("b3=" ++ Any.toString(b3))
