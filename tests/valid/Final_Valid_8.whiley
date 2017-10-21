// Test local variable initialisation
function id(int x) -> (int r):
    //
    final int y
    //
    while x < 10:
        y = x
        x = x + 1
    //
    return x

public export method test():
    assume id(-1) == 10
    assume id(0) == 10
    assume id(1) == 10