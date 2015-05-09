
function f({int} xs) -> int
requires xs ⊆ {1, 2, 3}:
    return |xs|

function g({int} ys) -> int:
    return f(ys & {1, 2, 3, 4})
