import println from whiley.lang.System

define nnint as [[int]]

{int} flattern([[int]] nnint):
    return { x | y in nnint, x in y }

void ::main(System.Console sys):
    iis = [[1,2,3],[3,4,5]]
    iis = flattern(iis)
    sys.out.println(Any.toString(iis))

