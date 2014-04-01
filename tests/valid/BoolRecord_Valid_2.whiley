import whiley.lang.System

method main(System.Console sys) => void:
    {bool flag, int code} x = {flag: true, code: 0}
    if x.flag:
        sys.out.println("GOT HERE")
