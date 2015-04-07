import whiley.lang.*

type Pos is (int, int)

function conflict(Pos p, int row, int col) -> bool:
    int r, int c = p
    if (r == row) || (c == col):
        return true
    int colDiff = Math.abs(c - col)
    int rowDiff = Math.abs(r - row)
    return colDiff == rowDiff

method main(System.Console sys) -> void:
    (int,int) p = (1, 2)
    bool c = conflict(p, 1, 2)
    sys.out.println(c)
    c = conflict(p, 3, 4)
    sys.out.println(c)
    c = conflict(p, 1, 4)
    sys.out.println(c)
    c = conflict(p, 3, 2)
    sys.out.println(c)
    c = conflict(p, 3, 3)
    sys.out.println(c)
