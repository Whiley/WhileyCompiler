import println from whiley.lang.System

method get() => int:
    return 1

method f() => [int]:
    return [1, 2, 3, get()]

method main(System.Console sys) => void:
    proc = new {state: 1}
    sys.out.println(Any.toString(f()))
