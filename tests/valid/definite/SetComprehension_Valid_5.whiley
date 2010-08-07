define nnint as [[int]]

{int} flattern([[int]] nnint):
    return { x | y in nnint, x in y }

void System::main([string] args):
    [[int]] iis = [[1,2,3],[3,4,5]]
    {int} is = flattern(iis)
    print str(is)

