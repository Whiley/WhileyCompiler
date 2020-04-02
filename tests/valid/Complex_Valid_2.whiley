type string is int[]

PieceKind PAWN = 0
PieceKind KNIGHT = 1
PieceKind BISHOP = 2
PieceKind ROOK = 3
PieceKind QUEEN = 4
PieceKind KING = 5
int[] PIECE_CHARS = ['P', 'N', 'B', 'R', 'Q', 'K']

type PieceKind is (int x) where PAWN <= x && x <= KING
type Piece is {bool colour, PieceKind kind}

Piece WHITE_PAWN = {colour: true, kind: PAWN}
Piece WHITE_KNIGHT = {colour: true, kind: KNIGHT}
Piece WHITE_BISHOP = {colour: true, kind: BISHOP}
Piece WHITE_ROOK = {colour: true, kind: ROOK}
Piece WHITE_QUEEN = {colour: true, kind: QUEEN}
Piece WHITE_KING = {colour: true, kind: KING}
Piece BLACK_PAWN = {colour: false, kind: PAWN}
Piece BLACK_KNIGHT = {colour: false, kind: KNIGHT}
Piece BLACK_BISHOP = {colour: false, kind: BISHOP}
Piece BLACK_ROOK = {colour: false, kind: ROOK}
Piece BLACK_QUEEN = {colour: false, kind: QUEEN}
Piece BLACK_KING = {colour: false, kind: KING}

type RowCol is int
type Pos is {RowCol col, RowCol row}

type SingleMove is {Pos to, Pos from, Piece piece}
type SingleTake is {Pos to, Piece taken, Pos from, Piece piece}

type SimpleMove is SingleMove | SingleTake
type CastleMove is {bool isWhite, bool kingSide}
type CheckMove is {Move check}
type Move is CheckMove | CastleMove | SimpleMove

Pos A1 = {col: 0, row: 0}
Pos A2 = {col: 0, row: 1}
Pos A3 = {col: 0, row: 2}
Pos D3 = {col: 3, row: 2}
Pos H1 = {col: 8, row: 1}

function append(int[] xs, int[] ys) -> (int[] zs)
ensures |zs| == |xs| + |ys|:
    //    
    int count = |xs| + |ys|
    int[] rs = [0; count]
    //
    int i = 0
    while i < |xs| where i >= 0 && i <= |xs| && |rs| == count:
        rs[i] = xs[i]
        i = i + 1
    //
    int j = 0
    while j < |ys| where j >= 0 && |rs| == count:
        rs[j + i] = ys[j]
        j = j + 1
    //
    return rs

function move2str(Move m) -> string:
    if m is SingleTake:
        string tmp = append(piece2str(m.piece),pos2str(m.from))
        tmp = append(tmp,"x")
        tmp = append(tmp,piece2str(m.taken))
        return append(tmp,pos2str(m.to))
    else:
        if m is SingleMove:
            string tmp = append(piece2str(m.piece),pos2str(m.from))
            tmp = append(tmp,"-")
            return append(tmp,pos2str(m.to))
        else:
            if m is CastleMove:
                if m.kingSide:
                    return "O-O"
                else:
                    return "O-O-O"
            else:
                return append(move2str(m.check),"+")

function piece2str(Piece p) -> string:
    if p.kind == PAWN:
        return ""
    else:
        return [PIECE_CHARS[p.kind]]

function pos2str(Pos p) -> string:
    return ['a' + p.col,'1' + p.row]

public export method test() :
    Move m = {to: A1, from: A2, piece: WHITE_PAWN}
    assume move2str(m) == "a2-a1"
    m = {to: A1, from: A2, piece: WHITE_KNIGHT}
    assume move2str(m) == "Na2-a1"
    m = {to: A1, taken: BLACK_KING, from: A2, piece: WHITE_QUEEN}
    assume move2str(m) == "Qa2xKa1"

