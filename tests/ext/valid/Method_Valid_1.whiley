define BLANK as 0
define CROSS as 1
define CIRCLE as 2

/**
 * A piece is either a blank, a cross or a circle.
 */
define Piece as { BLANK, CROSS, CIRCLE }

/**
 * A Board is a 3x3 grid of pieces
 */
public define Board as [Piece] where |$| == 9 && 
	numPieces($,CIRCLE) >= numPieces($,CROSS) &&
	numPieces($,CIRCLE) <= (numPieces($,CROSS)+1) 

Board EmptyBoard():
    return [BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK]

/**
 * Count the number of pieces which have been placed on the board.
 */
public int numPieces(Board board, Piece piece):
	count = 0
	for p in board:
		if p == piece:
			count = count + 1
	return count

Board ::update(System.Console console, Board b):
    b[0] = CIRCLE
    b[1] = CROSS
    b[0] = CIRCLE
    return b

void ::main(System.Console console):
    b = EmptyBoard()
    b = update(console,b)
    debug "" + b