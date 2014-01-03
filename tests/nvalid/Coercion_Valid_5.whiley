import println from whiley.lang.System

function f({int=>real} x) => {(int, real)}:
    return x

method main(System.Console sys) => void:
    x = f({1=>2.2, 2=>3.3})
    sys.out.println(Any.toString(x))
