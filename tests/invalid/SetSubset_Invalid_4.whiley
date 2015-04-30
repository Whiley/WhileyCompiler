
function f({real} x, {int} ys) -> void
requires x ⊂ ys:
    debug "X IS A SUBSET"

method main():
    f({1.0, 2.0}, {1, 2, 3})
    f({1.0, 4.0}, {1, 2, 3})
