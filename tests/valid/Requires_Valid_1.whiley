import whiley.lang.System
import string from whiley.lang.ASCII
import char from whiley.lang.ASCII

function f(int x) -> int:
    return x + 1

function g(int x, int y) -> (ASCII.string, string)
requires y == f(x):
    //
    return (Any.toString(x), Any.toString(y))

method main(System.Console sys) -> void:
    ASCII.string x, string y = g(1, f(1))
    debug x ++ "\n"
    debug y ++ "\n"
