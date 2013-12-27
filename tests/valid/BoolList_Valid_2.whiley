import * from whiley.lang.*

[bool] play([bool] board):
    nboard = board
    for i in 0..|board|:
        if isAlive(i,board):
            nboard[i] = true
        else:
            nboard[i] = false
    return nboard

bool isAlive(int i, [bool] board):
    if i>0 && (i+1) < |board| && board[i-1] && board[i+1]:
        return true
    else:
        return false

void ::main(System.Console console):
    xs = [true,true,true,true,true,true,true]
    for i in 0..5:
        console.out.println(xs)
        xs = play(xs)
    


    

