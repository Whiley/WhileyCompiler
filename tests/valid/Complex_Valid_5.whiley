constant BLANK is 0
constant CIRCLE is 1
constant CROSS is 2

type nat is (int x) where x >= 0

type Piece is (int p) where (p == BLANK) || p == CIRCLE || p == CROSS

type Board is ({Piece[] pieces, nat move} b) where (b.move <= 9) && (|b.pieces| == 9)

type EmptyBoard is (Board b) where all { i in 0..|b.pieces| | b.pieces[i] == BLANK }

function EmptyBoard() -> EmptyBoard:
    return {pieces: [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK], move: 0}

function play(Board b, Piece p, nat pos) -> Board
requires pos < 9:
    b.pieces[pos] = p
    return b

public export method test() :
    Board b = EmptyBoard()
    b = play(b, CIRCLE, 0)
    assume b == {move:0,pieces:[1, 0, 0, 0, 0, 0, 0, 0, 0]}
