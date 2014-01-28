import whiley.lang.System

type anat is (int x) where x >= 0

type bnat is (int x) where (2 * x) >= x

function atob(anat x) => bnat:
    return x

function btoa(bnat x) => anat:
    return x

method main(System.Console sys) => void:
    int x = 1
    sys.out.println(Any.toString(atob(x)))
    sys.out.println(Any.toString(btoa(x)))
