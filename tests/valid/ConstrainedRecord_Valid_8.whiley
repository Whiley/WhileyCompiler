constant BLANK is 0
constant CIRCLE is 1
constant CROSS is 2

type nat is (int x) where x >= 0

type piece is (int p) where p == BLANK || p == CIRCLE || p == CROSS

type Board is ({piece[] pieces, nat move} b) where (b.move <= 9) && (|b.pieces| == 9)

type EmptyBoard is (Board b) where no { i in 0..|b.pieces| | b.pieces[i] != BLANK }

function EmptyBoard() -> EmptyBoard:
    return {pieces: [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK], move: 0}

public export method test() :
    Board b = EmptyBoard()
    assert b.pieces[0] == BLANK
    assert b.pieces[1] == BLANK
    assert b.pieces[8] == BLANK
