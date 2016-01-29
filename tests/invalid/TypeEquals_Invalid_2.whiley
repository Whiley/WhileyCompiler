function f(bool e) -> int[]:
    if e is bool:
        return "bool"
    else:
        if e is int:
            return "int"
        else:
            return "int[]"
