import println from whiley.lang.System

define BLANK as 0
define CIRCLE as 1
define CROSS as 2

define nat as int where $ >= 0
define Piece as int where $ == BLANK || $ == CIRCLE || $ == CROSS

define Board as {
    nat move,
    [Piece] pieces 
} where move <= 9 && |pieces| == 9 // 3 x 3

define EmptyBoard as Board where no { x in $.pieces | x != BLANK }

EmptyBoard EmptyBoard():
    return {
        move: 0,
        pieces: [BLANK,BLANK,BLANK,
                 BLANK,BLANK,BLANK,
                 BLANK,BLANK,BLANK]
    }

Board play(Board b, Piece p, nat pos) requires pos < 9:
    b.pieces[pos] = p
    return b

void ::main(System.Console console):
    b = EmptyBoard()
    b = play(b,CIRCLE,0)
    console.out.println("BOARD: " + b)






    
    
    