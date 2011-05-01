define PAWN as 0
define KNIGHT as 1 
define BISHOP as 2
define ROOK as 3
define QUEEN as 4
define KING as 5

define PieceKind as { PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING }
define Piece as { PieceKind kind, bool colour }

define WHITE_PAWN as { kind: PAWN, colour: true }

define RowCol as int // where 0 <= $ && $ <= 8
define Pos as { RowCol col, RowCol row } 
define Square as Piece | null
define Row as [Square] // where |$| == 8
define Board as {
    [Row] rows, 
    bool flag
}    

Board f(Board board):
    board.rows[0][0] = WHITE_PAWN
    return board

void System::main([string] args):
    r1 = {rows: [[WHITE_PAWN]], flag: false }
    out->println(str(f(r1)))
