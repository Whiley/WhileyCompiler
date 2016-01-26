constant PAWN is 0
constant KNIGHT is 1
constant BISHOP is 2
constant ROOK is 3
constant QUEEN is 4
constant KING is 5

type PieceKind is (int x) where PAWN <= x && x <= KING
type Piece is {bool colour, PieceKind kind}

constant WHITE_PAWN is {colour: true, kind: PAWN}

constant BLACK_PAWN is {colour: false, kind: PAWN}

type Board is {bool flag, Piece[] rows}

function f(Board board) -> Board
requires |board.rows| > 0:
    //
    board.rows[0] = BLACK_PAWN
    return board

public export method test() :
    Board r1 = {flag: false, rows: [WHITE_PAWN]}
    assume f(r1) == {flag:false,rows:[{colour:false,kind:0}]}
