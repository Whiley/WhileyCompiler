import println from whiley.lang.System

define PAWN as 0
define KNIGHT as 1 
define BISHOP as 2
define ROOK as 3
define QUEEN as 4
define KING as 5

define PieceKind as { PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING }
define Piece as { PieceKind kind, bool colour }

define WHITE_PAWN as { kind: PAWN, colour: true }
define BLACK_PAWN as { kind: PAWN, colour: false }

define Board as {
    [Piece] rows, 
    bool flag
}    

Board f(Board board):
    board.rows[0] = BLACK_PAWN
    return board

void ::main(System.Console sys):
    r1 = {rows: [WHITE_PAWN], flag: false }
    sys.out.println(Any.toString(f(r1)))
