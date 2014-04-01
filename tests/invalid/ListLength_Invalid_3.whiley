
function f(int x) => (int y)
requires (x + 1) > 0
ensures y < 0:
    //
    debug Any.toString(x)
    return -1

method main(System.Console sys) => void:
    f(|sys.args| - 1)
