import println from whiley.lang.System

define nat as int where $ >= 0

// ==================================================================
// A square on the board is either blank, or holds either a circle or
// cross.
// ==================================================================
define BLANK as 0
define CIRCLE as 1
define CROSS as 2

define Square as int where $ == BLANK || $ == CIRCLE || $ == CROSS

// ==================================================================
// A board consists of 9 squares, and a move counter
// ==================================================================
define Board as {
    nat move,
    [Square] pieces // 3 x 3
} where |pieces| == 9 && move <= 9 && 
    countOf(pieces,BLANK) == (9 - move) &&
    (countOf(pieces,CIRCLE) == countOf(pieces,CROSS) ||    
     countOf(pieces,CIRCLE) == countOf(pieces,CROSS)+1)

// ==================================================================
// An empty board is one where all pieces are blank
// ==================================================================
Board EmptyBoard() ensures $.move == 0:
    return {
        move: 0,
        pieces: [BLANK,BLANK,BLANK,
                 BLANK,BLANK,BLANK,
                 BLANK,BLANK,BLANK]
    }

// ===============================================================
// Playing a piece requires an blank square, and returns the board
// updated with the piece at that position and an incremented the move
// counter.
// ===============================================================
Board play(Board b, nat pos) 
    requires pos < 9 && b.move < 9 && b.pieces[pos] == BLANK,
    ensures $.move == b.move + 1:
    // decide who's moving
    if b.move % 2 == 0:
        // circle on even moves
        b.pieces[pos] = CIRCLE
    else:
        // cross on odd moves
        b.pieces[pos] = CROSS
    // update the move counter
    b.move = b.move + 1
    // done
    return b

// ===============================================================
// Helper Method
// ===============================================================
int countOf([Square] pieces, Square s) ensures $ == |{ i | i in 0..|pieces|, pieces[i] == s }|:
    matches = { i | i in 0..|pieces|, pieces[i] == s }
    return |matches|

// ===============================================================
// Test Game
// ===============================================================
define GAME as [0,1,2,3,4,5,6,7,8]

void ::main(System.Console console):
    b = EmptyBoard()
    for p in GAME:
        console.out.println("BOARD: " + b)
        console.out.println("MOVE: " + p)
        if p < 0 || p > 9 || b.pieces[p] != BLANK || b.move == 9:
            console.out.println("INVALID MOVE!")
            break
        else:
            b = play(b,p)





    
    
    