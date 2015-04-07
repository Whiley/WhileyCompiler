import whiley.lang.*

method main(System.Console sys) -> void:
    {bool flag, int code} x = {flag: true, code: 0}
    sys.out.println(x)
    x.flag = false
    sys.out.println(x)
