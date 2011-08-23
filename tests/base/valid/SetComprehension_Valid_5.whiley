import whiley.lang.*:*

define nnint as [[int]]

{int} flattern([[int]] nnint):
    return { x | y in nnint, x in y }

void ::main(System sys,[string] args):
    iis = [[1,2,3],[3,4,5]]
    iis = flattern(iis)
    sys.out.println(str(iis))

