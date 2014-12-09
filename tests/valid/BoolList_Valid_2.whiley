import * from whiley.lang.*

function play([bool] board) -> [bool]:
    [bool] nboard = board
    for i in 0 .. |board|:
        if isAlive(i, board):
            nboard[i] = true
        else:
            nboard[i] = false
    return nboard

function isAlive(int i, [bool] board) -> bool:
    if (i > 0) && (((i + 1) < |board|) && (board[i - 1] && board[i + 1])):
        return true
    else:
        return false

method main(System.Console console) -> void:
    [bool] xs = [true, true, true, true, true, true, true]
    for i in 0 .. 5:
        console.out.println(xs)
        xs = play(xs)
