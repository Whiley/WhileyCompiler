import println from whiley.lang.System

type Pos is (int, int)

function conflict(Pos p, int row, int col) => bool:
    (r, c) = p
    if (r == row) || (c == col):
        return true
    colDiff = Math.abs(c - col)
    rowDiff = Math.abs(r - row)
    return colDiff == rowDiff

method main(System.Console sys) => void:
    p = (1, 2)
    c = conflict(p, 1, 2)
    sys.out.println(c)
    c = conflict(p, 3, 4)
    sys.out.println(c)
    c = conflict(p, 1, 4)
    sys.out.println(c)
    c = conflict(p, 3, 2)
    sys.out.println(c)
    c = conflict(p, 3, 3)
    sys.out.println(c)
