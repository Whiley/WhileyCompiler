define nnint as [[int]]

{int} flattern([[int]] nnint):
    return { x | y in nnint, x in y }

void System::main([string] args):
    iis = [[1,2,3],[3,4,5]]
    is = flattern(iis)
    out.println(str(is))

