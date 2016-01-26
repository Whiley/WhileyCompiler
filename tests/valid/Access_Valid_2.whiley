

type liststr is int[] | int[]

function index(liststr l, int index) -> any
    requires index >= 0 && index < |l|:
    //
    return l[index]

public export method test() :
    int[] l = [1, 2, 3]
    assume index(l, 0) == 1
    assume index(l, 1) == 2
    assume index(l, 2) == 3
    int[] s = "Hello World"
    assume index(s, 0) == 'H'
    assume index(s, 1) == 'e'    
    assume index(s, 2) == 'l'
