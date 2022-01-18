final piece BLANK = 0
final piece CIRCLE = 1
final piece CROSS = 2

type nat is (int x) where x >= 0

type piece is (int p) where p == BLANK || p == CIRCLE || p == CROSS

type Board is ({piece[] pieces, nat move} b) where (b.move <= 9) && (|b.pieces| == 9)

type EmptyBoard is (Board b) where all { i in 0..|b.pieces| | b.pieces[i] == BLANK }

function EmptyBoard() -> EmptyBoard:
    return {pieces: [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK], move: 0}

public export method test() :
    Board b = EmptyBoard()
    assert b.pieces[0] == BLANK
    assert b.pieces[1] == BLANK
    assert b.pieces[8] == BLANK
