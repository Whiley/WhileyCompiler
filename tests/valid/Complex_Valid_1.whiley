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

type RowCol is (int x) where x >= 0 && x < 8

type Pos is {RowCol col, RowCol row}

type Square is Piece | null

type Row is (Square[] squares) where |squares| == 8

type Board is ({
    bool blackCastleKingSide, 
    bool whiteCastleQueenSide, 
    bool blackCastleQueenSide, 
    Row[] rows, 
    bool whiteCastleKingSide
} b) where |b.rows| == 8

Row[] startingChessRows = [
    [WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK], 
    [WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN], 
    [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], 
    [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], 
    [BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN],
    [BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK]
]

Board startingChessBoard = {blackCastleKingSide: true, whiteCastleQueenSide: true, blackCastleQueenSide: true, rows: startingChessRows, whiteCastleKingSide: true}

function sign(int x, int y) -> (int r)
ensures x < y ==> r == 1
ensures x >= y ==> r == -1:
    //
    if x < y:
        return 1
    else:
        return -1

function clearRowExcept(Pos from, Pos to, Board board) -> bool:
    if (from.row != to.row) || (from.col == to.col):
        return false
    int row = from.row
    int col = from.col
    int end = to.col
    if col > end:
        col,end = end,col
    //
    while col < end where col >= 0:
        if board.rows[row][col] is null:
            col = col + 1
        else:
            return false
    //
    return true

Pos A1 = {col: 1, row: 1}
Pos H1 = {col: 7, row: 1}
Pos A3 = {col: 1, row: 3}
Pos D3 = {col: 4, row: 3}

public export method test() :
    assume clearRowExcept(A1, H1, startingChessBoard) == false
    assume clearRowExcept(A3, D3, startingChessBoard) == true
