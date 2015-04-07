import whiley.lang.*

type Board is [[bool]]

function update(Board board) -> Board:
    [[bool]] nboard = board
    for i in 0 .. 3:
        for j in 0 .. 3:
            int c = countLiving(board, i, j)
            if board[i][j]:
                switch c:
                    case 0, 1:
                        nboard[i][j] = false
                    case 2, 3:
    return nboard

function countLiving(Board board, int row, int col) -> int:
    int count = isAlive(board, row - 1, col - 1)
    count = count + isAlive(board, row - 1, col)
    count = count + isAlive(board, row - 1, col + 1)
    count = count + isAlive(board, row, col - 1)
    count = count + isAlive(board, row, col + 1)
    count = count + isAlive(board, row + 1, col - 1)
    count = count + isAlive(board, row + 1, col)
    count = count + isAlive(board, row + 1, col + 1)
    return count

function isAlive(Board board, int row, int col) -> int:
    int nrows = |board|
    if (row < 0) || (row >= nrows):
        return 0
    int ncols = |board[0]|
    if (col < 0) || (col >= ncols):
        return 0
    if board[row][col]:
        return 1
    else:
        return 0

method main(System.Console sys) -> void:
    [[bool]] board = [[false, true, false], [false, true, false], [false, true, false]]
    [[bool]] nboard = update(board)
    sys.out.println(board)
    sys.out.println(nboard)
