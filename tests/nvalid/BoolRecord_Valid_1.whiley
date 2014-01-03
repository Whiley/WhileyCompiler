import println from whiley.lang.System

method main(System.Console sys) => void:
    x = {flag: true, code: 0}
    sys.out.println(Any.toString(x))
    x.flag = false
    sys.out.println(Any.toString(x))
