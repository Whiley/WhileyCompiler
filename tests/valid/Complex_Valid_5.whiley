import whiley.lang.*

constant BLANK is 0

constant CIRCLE is 1

constant CROSS is 2

type nat is (int x) where x >= 0

type Piece is (int p) where (p == BLANK) || p == CIRCLE || p == CROSS

type Board is {[Piece] pieces, nat move} where (move <= 9) && (|pieces| == 9)

type EmptyBoard is (Board b) where no { x in b.pieces | x != BLANK }

function EmptyBoard() -> EmptyBoard:
    return {pieces: [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK], move: 0}

function play(Board b, Piece p, nat pos) -> Board
requires pos < 9:
    b.pieces[pos] = p
    return b

method main(System.Console console) -> void:
    Board b = EmptyBoard()
    b = play(b, CIRCLE, 0)
    console.out.println_s("BOARD: " ++ Any.toString(b))
