type Pos is (int, int)

function abs(int x) -> int:
    if x >= 0:
        return x
    else:
        return -x

function conflict(Pos p, int row, int col) -> bool:
    int r, int c = p
    if (r == row) || (c == col):
        return true
    int colDiff = abs(c - col)
    int rowDiff = abs(r - row)
    return colDiff == rowDiff

public export method test() -> void:
    (int,int) p = (1, 2)
    assume conflict(p, 1, 2)
    assume conflict(p, 3, 4)
    assume conflict(p, 1, 4)
    assume conflict(p, 3, 2)
    assume !conflict(p, 3, 3)

