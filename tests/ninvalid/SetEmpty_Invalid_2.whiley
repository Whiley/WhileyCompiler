
function f({int} xs) => void
requires xs != {}:
    debug Any.toString(xs)

method main(System.Console sys) => void:
    f({1, 4})
    f({})
