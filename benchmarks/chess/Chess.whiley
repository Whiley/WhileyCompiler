// A simple chess model
//
// David J. Pearce, 2010

// =============================================================
// Pieces
// =============================================================

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
define Board as [Row] // where |$| == 8

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

// =============================================================
// Moves
// =============================================================

define SingleMove as { Piece piece, Pos from, Pos to }
define SingleTake as { Piece piece, Pos from, Pos to, Piece taken }
define SimpleMove as SingleMove | SingleTake

define CheckMove as { SimpleMove check }
define Move as CheckMove | SimpleMove

// castling
// en passant

// =============================================================
// Valid Move Dispatch
// =============================================================

// The purpose of the validMove method is to check whether or not a
// move is valid on a given board.
bool validMove(Move move, Board board):
    if move ~= SingleTake:
        return validPieceMove(move.piece,move.from,move.to,true,board) &&
            validPiece(move.taken,move.to,board) && !inCheck(!(move.piece.colour), applyMove(move,board))
    else if move ~= SingleMove:
        return validPieceMove(move.piece,move.from,move.to,false,board) &&
            squareAt(move.to,board) ~= null && !inCheck(!(move.piece.colour), applyMove(move,board))
    else if move ~= CheckMove:
        m = move.check
        return validPieceMove(m.piece,m.from,m.to,false,board) && inCheck(!(m.piece.colour), applyMove(m,board))
    return false

bool validPieceMove(Piece piece, Pos from, Pos to, bool isTake, Board board):
    if validPiece(piece,from,board):
        if piece.kind == PAWN:
            return validPawnMove(piece.colour,from,to,isTake,board)        
        else if piece.kind == KNIGHT:
            return validKnightMove(piece.colour,from,to,isTake,board)
        else if piece.kind == BISHOP:
            return validBishopMove(piece.colour,from,to,isTake,board)
        else if piece.kind == ROOK:
            return validRookMove(piece.colour,from,to,isTake,board)
        else if piece.kind == QUEEN:
            return validQueenMove(piece.colour,from,to,isTake,board)
        else if piece.kind == KING:
            return validKingMove(piece.colour,from,to,isTake,board)
    return false

// Check whether a given piece is actually at a given position in the
// board.
bool validPiece(Piece piece, Pos pos, Board board):
    sq = squareAt(pos,board)
    if sq ~= null:
        return false
    else:
        return sq == piece

// Determine whether the board is in check after the given move, with
// respect to the opposite colour of the move.
bool inCheck(bool isWhite, Board board):
    if isWhite:
        kpos = findPiece(WHITE_KING,board)
    else:
        kpos = findPiece(BLACK_KING,board)    
    if kpos ~= null:
        return false // dead-code!
    // check every possible piece cannot take king
    for r in range(0,8):
        for c in range(0,8):
            tmp = board[r][c]
            if !(tmp ~= null) && tmp.colour == !isWhite && 
                validPieceMove(tmp,{row: r, col: c},kpos,true,board):
                return true
    // no checks found
    return false

// =============================================================
// Individual Piece Moves
// =============================================================

bool validPawnMove(bool isWhite, Pos from, Pos to, bool isTake, Board board):
    // calculate row difference
    if (isWhite):
        rowdiff = to.row - from.row
    else:
        rowdiff = from.row - to.row        
    // check row difference either 1 or 2, and column 
    // fixed (unless take)
    if rowdiff <= 0 || rowdiff > 2 || (!isTake && from.col != to.col):
        return false
    // check that column difference is one for take
    if isTake && from.col != (to.col - 1) && from.col != (to.col + 1):
        return false
    // check if rowdiff is 2 that on the starting rank
    if isWhite && rowdiff == 2 && from.row != 1:
        return false
    else if !isWhite && rowdiff == 2 && from.row != 6:
        return false
    // looks like we're all good
    return true    

bool validKnightMove(bool isWhite, Pos from, Pos to, bool isTake, Board board):
    diffcol = max(from.col,to.col) - min(from.col,to.col)
    diffrow = max(from.row,to.row) - min(from.row,to.row)
    return (diffcol == 2 && diffrow == 1) || (diffcol == 1 && diffrow == 2)

bool validBishopMove(bool isWhite, Pos from, Pos to, bool isTake, Board board):
    return clearDiaganolExcept(from,to,board)

bool validRookMove(bool isWhite, Pos from, Pos to, bool isTake, Board board):
    return clearRowExcept(from,to,board) || clearColumnExcept(from,to,board)

bool validQueenMove(bool isWhite, Pos from, Pos to, bool isTake, Board board):
    return clearRowExcept(from,to,board) || clearColumnExcept(from,to,board) ||
        clearDiaganolExcept(from,to,board)

bool validKingMove(bool isWhite, Pos from, Pos to, bool isTake, Board board):
    diffcol = max(from.col,to.col) - min(from.col,to.col)
    diffrow = max(from.row,to.row) - min(from.row,to.row)
    return diffcol == 1 || diffrow == 1

// =============================================================
// Apply Move
// =============================================================

Board applyMove(Move move, Board board):
    if move ~= SingleMove:
        // SingleTake is processed in the same way
        return applySingleMove(move,board)
    return board

Board applySingleMove(SingleMove move, Board board):
    from = move.from
    to = move.to
    board[from.row][from.col] = null
    board[to.row][to.col] = move.piece
    return board

// =============================================================
// Helper Functions
// =============================================================

Square squareAt(Pos p, Board b):
    return b[p.row][p.col]

// The following method checks whether a given row is completely
// clear, excluding the end points. Observe that this doesn't
// guarantee a given diaganol move is valid, since this function does not
// ensure anything about the relative positions of the given pieces.
bool clearRowExcept(Pos from, Pos to, Board board):
    // check this is really a row
    if from.row != to.row || from.col == to.col:
        return false
    inc = sign(from.col,to.col)
    row = from.row
    col = from.col + inc
    while col != to.col:
        if board[row][col] ~= null:
            col = col + inc
        else:
            return false        
    return true

// The following method checks whether a given column is completely
// clear, excluding the end points. Observe that this doesn't
// guarantee a given diaganol move is valid, since this function does not
// ensure anything about the relative positions of the given pieces.
bool clearColumnExcept(Pos from, Pos to, Board board):
    if from.col != to.col || from.row == to.row:
        return false
    inc = sign(from.row,to.row)
    row = from.row + inc
    col = from.col
    while row != to.row:
        if board[row][col] ~= null:
            row = row + inc
        else:
            return false            
    return true

// The following method checks whether the given diaganol is completely
// clear, excluding the end points. Observe that this doesn't
// guarantee a given diaganol move is valid, since this function does not
// ensure anything about the relative positions of the given pieces.
bool clearDiaganolExcept(Pos from, Pos to, Board board):
    // check this is really a diaganol
    diffcol = max(from.col,to.col) - min(from.col,to.col)
    diffrow = max(from.row,to.row) - min(from.row,to.row)
    if diffcol != diffrow:
        return false
    // determine the col and row signs
    colinc = sign(from.col,to.col)
    rowinc = sign(from.row,to.row)
    // finally, walk the line!
    row = from.row + rowinc
    col = from.col + colinc
    while row != to.row && col != to.col:
        if board[row][col] ~= null:
            col = col + colinc
            row = row + rowinc
        else:
            return false
    // ok, looks like we're clear
    return true 

int sign(int x, int y):
    if x < y:
        return 1
    else:
        return -1
    
// This method finds a given piece.  It's used primarily to locate
// kings on the board to check if they are in check.
Pos|null findPiece(Piece p, Board b):
    for r in range(0,8):
        for c in range(0,8):
            tmp = b[r][c]    
            // FIXME: shouldn't need to check against null
            if !(tmp ~= null) && tmp == p:
                // ok, we've located the piece
                return { row: r, col: c }            
    // could find the piece
    return null

// range should be built in
[int] range(int start, int end):
    r = []
    while start < end:
        r = r + [start]
        start = start + 1
    return r

int max(int a, int b):
    if a < b:
        return b
    else:
        return a

int min(int a, int b):
    if a > b:
        return b
    else:
        return a
