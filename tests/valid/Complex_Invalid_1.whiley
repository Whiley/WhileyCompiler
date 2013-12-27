import println from whiley.lang.System

define PAWN as 0
define KNIGHT as 1 
define BISHOP as 2
define ROOK as 3
define QUEEN as 4
define KING as 5
define PIECE_CHARS as [ 'P', 'N', 'B', 'R', 'Q', 'K' ]

define PieceKind as { PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING }
define Piece as { PieceKind kind, bool colour }

define WHITE_PAWN as { kind: PAWN, colour: true }
define WHITE_KNIGHT as { kind: KNIGHT, colour: true }
define WHITE_BISHOP as { kind: BISHOP, colour: true }
define WHITE_ROOK as { kind: ROOK, colour: true }
define WHITE_QUEEN as { kind: QUEEN, colour: true }
define WHITE_KING as { kind: KING, colour: true }

define BLACK_PAWN as { kind: PAWN, colour: false }
define BLACK_KNIGHT as { kind: KNIGHT, colour: false }
define BLACK_BISHOP as { kind: BISHOP, colour: false }
define BLACK_ROOK as { kind: ROOK, colour: false }
define BLACK_QUEEN as { kind: QUEEN, colour: false }
define BLACK_KING as { kind: KING, colour: false }

// =============================================================
// Positions
// =============================================================

define RowCol as int // where 0 <= $ && $ <= 8
define Pos as { RowCol col, RowCol row } 

// =============================================================
// board
// =============================================================

define Square as Piece | null
define Row as [Square] // where |$| == 8
define Board as {
    [Row] rows, 
    bool whiteCastleKingSide,
    bool whiteCastleQueenSide,
    bool blackCastleKingSide,
    bool blackCastleQueenSide
}    

define startingChessRows as [
    [ WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK ], // rank 1
    [ WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN ],          // rank 2
    [ null, null, null, null, null, null, null, null ],                                                   // rank 3
    [ null, null, null, null, null, null, null, null ],                                                   // rank 4
    [ null, null, null, null, null, null, null, null ],                                                   // rank 5
    [ null, null, null, null, null, null, null, null ],                                                   // rank 6
    [ BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN ],          // rank 7
    [ BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK ]  // rank 8
]

define startingChessBoard as {
    rows: startingChessRows,
    whiteCastleKingSide: true,
    whiteCastleQueenSide: true,
    blackCastleKingSide: true,
    blackCastleQueenSide: true
}

int sign(int x, int y):
    if x < y:
        return 1
    else:
        return -1   

bool clearRowExcept(Pos from, Pos to, Board board):
    // check this is really a row
    if from.row != to.row || from.col == to.col:
        return false
    inc = sign(from.col,to.col)
    row = from.row
    col = from.col + inc
    while col != to.col:
        if board.rows[row][col] is null:
            col = col + inc
        else:
            return false        
    return true


define A1 as { col: 1, row: 1 }
define H1 as { col: 8, row: 1 }

define A3 as { col: 1, row: 3 }
define D3 as { col: 4, row: 3 }


void ::main(System.Console sys):
    r = clearRowExcept(A1,H1,startingChessBoard)
    sys.out.println("GOT: " + Any.toString(r))
    r = clearRowExcept(A3,D3,startingChessBoard)
    sys.out.println("GOT: " + Any.toString(r))
