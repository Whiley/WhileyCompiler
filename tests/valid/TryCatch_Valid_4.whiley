import whiley.lang.System

type Error is {string msg}

constant PAWN is 0

constant KNIGHT is 1

constant BISHOP is 2

constant ROOK is 3

constant QUEEN is 4

constant KING is 5

constant PIECE_CHARS is ['P', 'N', 'B', 'R', 'Q', 'K']

constant PieceKind is {PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING}

type Piece is {bool colour, PieceKind kind}

constant WHITE_PAWN is {colour: true, kind: PAWN}

constant WHITE_KNIGHT is {colour: true, kind: KNIGHT}

constant WHITE_BISHOP is {colour: true, kind: BISHOP}

constant WHITE_ROOK is {colour: true, kind: ROOK}

constant WHITE_QUEEN is {colour: true, kind: QUEEN}

constant WHITE_KING is {colour: true, kind: KING}

constant BLACK_PAWN is {colour: false, kind: PAWN}

constant BLACK_KNIGHT is {colour: false, kind: KNIGHT}

constant BLACK_BISHOP is {colour: false, kind: BISHOP}

constant BLACK_ROOK is {colour: false, kind: ROOK}

constant BLACK_QUEEN is {colour: false, kind: QUEEN}

constant BLACK_KING is {colour: false, kind: KING}

type Move is {Pos to, Pos from, Piece piece}

type Pos is {int col, int row}

constant POS is {col: 0, row: 0}

type InvalidMove is {Board board, Move move}

public function InvalidMove(Move m) => InvalidMove:
    return {board: startingChessBoard, move: m}

type FilePos is {int col}

type ShortPos is Pos | FilePos | null

type ShortMove is {Pos to, bool isTake, ShortPos from, Piece piece}

type InvalidShortMove is {Board board, ShortMove move}

public function InvalidShortMove(ShortMove m) => InvalidShortMove:
    return {board: startingChessBoard, move: m}

type Square is Piece | null

type Row is [Square]

type Board is [Row]

constant startingChessBoard is [[WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK], [WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN], [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], [BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN], [BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK]]

function f(int x) => int throws InvalidMove | InvalidShortMove:
    if x > 0:
        return 1
    else:
        if x == 0:
            throw InvalidShortMove({to: POS, isTake: false, from: POS, piece: WHITE_ROOK})
        else:
            throw InvalidMove({to: POS, from: POS, piece: WHITE_ROOK})

method g(System.Console sys, int x) => void:
    try:
        f(x)
    catch(InvalidMove e):
        sys.out.println("CAUGHT EXCEPTION (InvalidMove)")
    catch(InvalidShortMove e):
        sys.out.println("CAUGHT EXCEPTION (InvalidShortMove)")

method main(System.Console sys) => void:
    g(sys, 1)
    g(sys, 0)
    g(sys, -1)
