import whiley.lang.*

constant BLANK is 0
constant CROSS is 1
constant CIRCLE is 2

type Piece is (int x) where x in {BLANK, CROSS, CIRCLE}

type Board is ([Piece] pieces) where |pieces| == 9

function EmptyBoard() -> Board:
    return [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK]

public function numPieces(Board board, Piece piece) -> int:
    int count = 0
    for p in board:
        if p == piece:
            count = count + 1
    return count

method update(System.Console console, Board b) -> Board:
    b[0] = CIRCLE
    b[1] = CROSS
    b[0] = CIRCLE
    return b

method main(System.Console console) -> void:
    Board b = EmptyBoard()
    b = update(console, b)
    debug "" ++ Any.toString(b)
