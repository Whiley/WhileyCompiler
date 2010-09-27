define PAWN as 0
define KNIGHT as 1 

define PieceKind as { PAWN, KNIGHT }
define Piece as { PieceKind kind, bool colour }

define Row as [Piece] where |$| == 8
define Board as [Row] where |$| == 8

bool validMove(Board board):
    return validSingleMove(board)

bool validSingleMove(Board board):
    return false // temporary
