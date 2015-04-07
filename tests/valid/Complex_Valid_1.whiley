import whiley.lang.*

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

type Square is Piece | null

type Row is [Square]

type Board is {bool blackCastleKingSide, bool whiteCastleQueenSide, bool blackCastleQueenSide, [Row] rows, bool whiteCastleKingSide}

constant startingChessRows is [[WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK], [WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN], [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], [null, null, null, null, null, null, null, null], [BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN], [BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK]]

constant startingChessBoard is {blackCastleKingSide: true, whiteCastleQueenSide: true, blackCastleQueenSide: true, rows: startingChessRows, whiteCastleKingSide: true}

function sign(int x, int y) -> int:
    if x < y:
        return 1
    else:
        return -1

function clearRowExcept(Pos from, Pos to, Board board) -> bool:
    if (from.row != to.row) || (from.col == to.col):
        return false
    int inc = sign(from.col, to.col)
    int row = from.row
    int col = from.col + inc
    //
    while col != to.col:
        if board.rows[row][col] is null:
            col = col + inc
        else:
            return false
    //
    return true

constant A1 is {col: 1, row: 1}

constant H1 is {col: 8, row: 1}

constant A3 is {col: 1, row: 3}

constant D3 is {col: 4, row: 3}

method main(System.Console sys) -> void:
    bool r = clearRowExcept(A1, H1, startingChessBoard)
    sys.out.println_s("GOT: " ++ Any.toString(r))
    r = clearRowExcept(A3, D3, startingChessBoard)
    sys.out.println_s("GOT: " ++ Any.toString(r))
