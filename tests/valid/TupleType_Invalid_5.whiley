import println from whiley.lang.System

define Pos as (int,int)

bool conflict(Pos p, int row, int col):
    r,c = p
    if r == row || c == col:
        return true
    colDiff = Math.abs(c - col)
    rowDiff = Math.abs(r - row)
    return colDiff == rowDiff
    
void ::main(System.Console sys):
    p = (1,2)
    c = conflict(p,1,2)
    sys.out.println(c)
    c = conflict(p,3,4)
    sys.out.println(c)
    c = conflict(p,1,4)
    sys.out.println(c)
    c = conflict(p,3,2)
    sys.out.println(c)
    c = conflict(p,3,3)
    sys.out.println(c)

    
