import whiley.lang.System

type nnint is [[int]]

function flattern([[int]] nnint) => {int}:
    return { x | y in nnint, x in y }

method main(System.Console sys) => void:
    [[int]] iis = [[1, 2, 3], [3, 4, 5]]
    {int} sis = flattern(iis)
    sys.out.println(Any.toString(sis))
