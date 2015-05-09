function f([real] args) -> [real]:
    return args

method g([real] args) -> [real]:
    [int] l = [1, 2, 3]
    [real|int] r = args ++ l
    return f(r)
