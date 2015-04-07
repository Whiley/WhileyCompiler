import whiley.lang.*

method get() -> int:
    return 1

method f() -> [int]:
    return [1, 2, 3, get()]

method main(System.Console sys) -> void:
    &{int state} proc = new {state: 1}
    sys.out.println(f())
