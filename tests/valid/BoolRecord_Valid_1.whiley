import whiley.lang.System

method main(System.Console sys) => void:
    {bool flag, int code} x = {flag: true, code: 0}
    sys.out.println(Any.toString(x))
    x.flag = false
    sys.out.println(Any.toString(x))
