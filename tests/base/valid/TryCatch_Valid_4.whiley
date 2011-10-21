import * from whiley.lang.*

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
define WHITE_ROOK as {kind: ROOK, colour: false}

define SingleMove as { Piece piece, Pos from, Pos to }
define SingleTake as { Piece piece, Pos from, Pos to, Piece taken }
define SimpleMove as SingleMove | SingleTake

define CastleMove as { bool isWhite, bool kingSide }
define CheckMove as { Move check }
define Move as CheckMove | CastleMove | SimpleMove

define RowCol as int // where 0 <= $ && $ <= 8
define Pos as { RowCol col, RowCol row } 
define POS as {col: 0, row: 0}

define InvalidMove as { Move move }
public InvalidMove InvalidMove(Move m):
    return { move: m }

// ================================================================

define RankPos as { int row }
define FilePos as { int col }
define ShortPos as Pos | RankPos | FilePos | null

define ShortSingleMove as { Piece piece, ShortPos from, Pos to, bool isTake }
define ShortCheckMove as { ShortMove check }

define ShortMove as ShortSingleMove | ShortCheckMove | CastleMove
define ShortRound as (ShortMove,ShortMove|null)

define InvalidShortMove as { ShortMove move }
public InvalidShortMove InvalidShortMove(ShortMove m):
    return { move: m }

// ================================================================

int f(int x) throws InvalidMove|InvalidShortMove:
    if x > 0:
        return 1
    else if x == 0:
        throw InvalidShortMove( {piece: WHITE_ROOK, from: POS, to: POS, isTake: false} )
    else:
        throw InvalidMove( {piece: WHITE_ROOK, from: POS, to: POS} )

void ::g(System sys, int x):
    try:
        f(x)
    catch(InvalidMove e):
        sys.out.println("CAUGHT EXCEPTION (InvalidMove)")
    catch(InvalidShortMove e):
        sys.out.println("CAUGHT EXCEPTION (InvalidShortMove)")
    
void ::main(System sys,[string] args):
    g(sys,1)
    g(sys,0)
    g(sys,-1)
