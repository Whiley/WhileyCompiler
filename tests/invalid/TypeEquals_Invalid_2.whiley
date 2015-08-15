function f(real e) -> int[]:
    if e is real:
        return "real"
    else:
        if e is int:
            return "int"
        else:
            return "int[]"
