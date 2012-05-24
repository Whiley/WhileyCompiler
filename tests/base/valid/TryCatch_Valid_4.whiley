import println from whiley.lang.System

define Error as {string msg}

// ================================================================

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

// ================================================================

define Move as { Piece piece, Pos from, Pos to }

define Pos as { int col, int row } 
define POS as {col: 0, row: 0}

define InvalidMove as { Move move, Board board }
public InvalidMove InvalidMove(Move m):
    return { move: m, board: startingChessBoard }

// ================================================================

define FilePos as { int col }
define ShortPos as Pos | FilePos | null
define ShortMove as { Piece piece, ShortPos from, Pos to, bool isTake }

define InvalidShortMove as { ShortMove move, Board board }
public InvalidShortMove InvalidShortMove(ShortMove m):
    return { move: m, board: startingChessBoard }

// ================================================================

define Square as Piece | null
define Row as [Square] // where |$| == 8
define Board as [Row]

define startingChessBoard as [
    [ WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK ], // rank 1
    [ WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN ],          // rank 2
    [ null, null, null, null, null, null, null, null ],                                                   // rank 3
    [ null, null, null, null, null, null, null, null ],                                                   // rank 4
    [ null, null, null, null, null, null, null, null ],                                                   // rank 5
    [ null, null, null, null, null, null, null, null ],                                                   // rank 6
    [ BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN ],          // rank 7
    [ BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK ]  // rank 8
]

// ================================================================

int f(int x) throws InvalidMove|InvalidShortMove:
    if x > 0:
        return 1
    else if x == 0:
        throw InvalidShortMove( {piece: WHITE_ROOK, from: POS, to: POS, isTake: false} )
    else:
        throw InvalidMove( {piece: WHITE_ROOK, from: POS, to: POS} )

void ::g(System.Console sys, int x):
    try:
        f(x)
    catch(InvalidMove e):
        sys.out.println("CAUGHT EXCEPTION (InvalidMove)")
    catch(InvalidShortMove e):
        sys.out.println("CAUGHT EXCEPTION (InvalidShortMove)")
    
void ::main(System.Console sys):
    g(sys,1)
    g(sys,0)
    g(sys,-1)
