import println from whiley.lang.System

define Board as [[bool]]

Board update(Board board):
    nboard = board
    for i in 0..3:
        for j in 0..3:
            c = countLiving(board,i,j)
            if board[i][j]:        
                switch c:
                    case 0,1:
                        // Any live cell with fewer than two live neighbours dies, 
                        // as if caused by under-population.
                        nboard[i][j] = false
                    case 2,3:
                        // Any live cell with two or three live neighbours lives
                        // on to the next generation.
                // end switch
    // done                    
    return nboard

int countLiving(Board board, int row, int col):
    count = isAlive(board,row-1,col-1)
    count = count + isAlive(board,row-1,col)
    count = count + isAlive(board,row-1,col+1)
    count = count + isAlive(board,row,col-1)
    count = count + isAlive(board,row,col+1)
    count = count + isAlive(board,row+1,col-1)
    count = count + isAlive(board,row+1,col)
    count = count + isAlive(board,row+1,col+1)
    return count

int isAlive(Board board, int row, int col):
    nrows = |board|
    if row < 0 || row >= nrows:
        return 0
    ncols = |board[0]|
    if col < 0 || col >= ncols:
        return 0
    if board[row][col]:
        return 1
    else:
        return 0

void ::main(System.Console sys):
    board = [[false,true,false],[false,true,false],[false,true,false]]
    nboard = update(board)
    sys.out.println(board)
    sys.out.println(nboard)

