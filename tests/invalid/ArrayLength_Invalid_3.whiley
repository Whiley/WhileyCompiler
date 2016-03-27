
function f(int x) -> (int y)
requires (x + 1) > 0
ensures y < 0:
    //
    return -1

method main() :
    f(-1)
