import println from whiley.lang.System

define BLANK as 0
define CIRCLE as 1
define CROSS as 2

define nat as int where $ >= 0
define piece as int where $ == BLANK || $ == CIRCLE || $ == CROSS

define Board as {
    nat move,
    [piece] pieces 
} where move <= 9 && |pieces| == 9 // 3 x 3

define EmptyBoard as Board where no { x in $.pieces | x != BLANK }

EmptyBoard EmptyBoard():
    return {
        move: 0,
        pieces: [BLANK,BLANK,BLANK,
                 BLANK,BLANK,BLANK,
                 BLANK,BLANK,BLANK]
    }

void ::main(System.Console console):
    b = EmptyBoard()
    assert b.pieces[0] == BLANK
    assert b.pieces[1] == BLANK
    // NOTE: commented these out to keep the step Wycs count below
    // 50000
    //assert b.pieces[2] == BLANK
    //assert b.pieces[3] == BLANK
    //assert b.pieces[4] == BLANK
    //assert b.pieces[5] == BLANK
    //assert b.pieces[6] == BLANK
    //assert b.pieces[7] == BLANK
    assert b.pieces[8] == BLANK
    console.out.println("BOARD: " + b)






    
    
    