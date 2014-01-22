
function f({int} ls) => void
requires some { i in ls | i < 0 }:
    debug Any.toString(ls)

method main(System.Console sys) => void:
    f({1, 2, 3})
