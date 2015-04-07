import whiley.lang.*

type string is [int]

constant PAWN is 0

constant KNIGHT is 1

constant BISHOP is 2

constant ROOK is 3

constant QUEEN is 4

constant KING is 5

constant PIECE_CHARS is ['P', 'N', 'B', 'R', 'Q', 'K']

type PieceKind is (int x) where x in {PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING}

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

type RowCol is int

type Pos is {RowCol col, RowCol row}

type SingleMove is {Pos to, Pos from, Piece piece}

type SingleTake is {Pos to, Piece taken, Pos from, Piece piece}

type SimpleMove is SingleMove | SingleTake

type CastleMove is {bool isWhite, bool kingSide}

type CheckMove is {Move check}

type Move is CheckMove | CastleMove | SimpleMove

constant A1 is {col: 0, row: 0}

constant A2 is {col: 0, row: 1}

constant A3 is {col: 0, row: 2}

constant D3 is {col: 3, row: 2}

constant H1 is {col: 8, row: 1}

function move2str(Move m) -> string:
    if m is SingleTake:
        return (((piece2str(m.piece) ++ pos2str(m.from)) ++ "x") ++ piece2str(m.taken)) ++ pos2str(m.to)
    else:
        if m is SingleMove:
            return ((piece2str(m.piece) ++ pos2str(m.from)) ++ "-") ++ pos2str(m.to)
        else:
            if m is CastleMove:
                if m.kingSide:
                    return "O-O"
                else:
                    return "O-O-O"
            else:
                if m is CheckMove:
                    return move2str(m.check) ++ "+"
                else:
                    return ""

function piece2str(Piece p) -> string:
    if p.kind == PAWN:
        return ""
    else:
        return [PIECE_CHARS[p.kind]]

function pos2str(Pos p) -> string:
    return ['a' + p.col,'1' + p.row]

method main(System.Console sys) -> void:
    Move m = {to: A1, from: A2, piece: WHITE_PAWN}
    sys.out.println_s(move2str(m))
    m = {to: A1, from: A2, piece: WHITE_KNIGHT}
    sys.out.println_s(move2str(m))
    m = {to: A1, taken: BLACK_KING, from: A2, piece: WHITE_QUEEN}
    sys.out.println_s(move2str(m))
