import whiley.lang.System

type nnint is [[int]]

function flattern([[int]] nnint) => {int}:
    return { x | y in nnint, x in y }

method main(System.Console sys) => void:
    iis = [[1, 2, 3], [3, 4, 5]]
    iis = flattern(iis)
    sys.out.println(Any.toString(iis))
