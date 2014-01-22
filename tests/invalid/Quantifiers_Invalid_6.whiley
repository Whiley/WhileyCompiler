
function f({int} ls) => void
requires no { i in ls | i <= 0 }:
    debug Any.toString(ls)

function g({int} ls) => void:
    f(ls)

method main(System.Console sys) => void:
    g({0, 1, 2, 3})
