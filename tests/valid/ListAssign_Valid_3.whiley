import whiley.lang.System

function update([[int]] ls) => [[int]]:
    ls[0][0] = 10
    return ls

function f([[int]] ls) => ([[int]], [[int]]):
    nls = update(ls)
    return (ls, nls)

method main(System.Console sys) => void:
    ls = [[1, 2, 3, 4]]
    (ls, nls) = f(ls)
    sys.out.println(Any.toString(ls))
    sys.out.println(Any.toString(nls))
