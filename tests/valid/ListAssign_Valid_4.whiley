PieceKind PAWN = 0
PieceKind KNIGHT = 1
PieceKind BISHOP = 2
PieceKind ROOK = 3
PieceKind QUEEN = 4
PieceKind KING = 5

type PieceKind is (int x) where PAWN <= x && x <= KING
type Piece is {bool colour, PieceKind kind}

Piece WHITE_PAWN = {colour: true, kind: PAWN}
Piece BLACK_PAWN = {colour: false, kind: PAWN}

type Board is {bool flag, Piece[] rows}

function f(Board board) -> Board
requires |board.rows| > 0:
    //
    board.rows[0] = BLACK_PAWN
    return board

public export method test() :
    Board r1 = {flag: false, rows: [WHITE_PAWN]}
    assume f(r1) == {flag:false,rows:[{colour:false,kind:0}]}
