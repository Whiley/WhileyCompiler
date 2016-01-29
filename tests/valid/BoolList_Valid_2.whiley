function play(bool[] board) -> bool[]:
    bool[] nboard = board
    int i = 0
    while i < |board| where i >= 0 && |board| == |nboard|:
        if isAlive(i, board):
            nboard[i] = true
        else:
            nboard[i] = false
        i = i + 1
    return nboard

function isAlive(int i, bool[] board) -> bool:
    if (i > 0) && (((i + 1) < |board|) && (board[i - 1] && board[i + 1])):
        return true
    else:
        return false

public export method test() :
    bool[] xs = [true, true, true, true, true, true, true]
    assume xs == [true, true, true, true, true, true, true]
    xs = play(xs)
    assume xs == [false, true, true, true, true, true, false]
    xs = play(xs)
    assume xs == [false, false, true, true, true, false, false]
    xs = play(xs)
    assume xs == [false, false, false, true, false, false, false]
    xs = play(xs)
    assume xs == [false, false, false, false, false, false, false]
