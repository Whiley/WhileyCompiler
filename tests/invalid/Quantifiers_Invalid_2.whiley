
function f({int} ls) => void
requires no { i in ls | i <= 0 }:
    debug Any.toString(ls)

method main(System.Console sys) => void:
    f({0, 1, 2, 3})
