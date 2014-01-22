import println from whiley.lang.System

constant BLANK is 0

constant CIRCLE is 1

constant CROSS is 2

type nat is int where $ >= 0

type piece is int where ($ == BLANK) || (($ == CIRCLE) || ($ == CROSS))

type Board is {[piece] pieces, nat move} where (move <= 9) && (|pieces| == 9)

type EmptyBoard is Board where no { x in $.pieces | x != BLANK }

function EmptyBoard() => EmptyBoard:
    return {pieces: [BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK], move: 0}

method main(System.Console console) => void:
    b = EmptyBoard()
    assert b.pieces[0] == BLANK
    assert b.pieces[1] == BLANK
    assert b.pieces[8] == BLANK
    console.out.println("BOARD: " + b)
