final Piece BLANK = 0
final Piece CROSS = 1
final Piece CIRCLE = 2

type Piece is (int x) where x == BLANK || x == CROSS || x == CIRCLE
type Board is (Piece[] pieces) where |pieces| == 9

function EmptyBoard() -> Board:
    return [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK]

function numPieces(Board board, Piece piece) -> int:
    int count = 0
    int i = 0
    while i < |board| where i >= 0:
        Piece p = board[i]
        if p == piece:
            count = count + 1
        i = i + 1
    return count

method update(Board b) -> Board:
    b[0] = CIRCLE
    b[1] = CROSS
    b[0] = CIRCLE
    return b

public export method test() :
    Board b = EmptyBoard()
    b = update(b)
    assume b == [2, 1, 0, 0, 0, 0, 0, 0, 0]
