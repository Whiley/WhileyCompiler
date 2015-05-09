

function f({int} xs, {int} ys, {int} zs) -> {int}:
    return xs

function g({int} ys) -> {int}:
    return f(ys, ys, ys)

function h({int} ys, {int} zs) -> {int}:
    return f(ys, zs, ys & zs)

public export method test() -> void:
    assume g({}) == {}
    assume g({2}) == {2}
    assume g({1, 2, 3}) == {1,2,3}
    assume h({}, {}) == {}
    assume h({1}, {1, 2}) == {1}
    assume h({1, 2, 3}, {3, 4, 5}) == {1,2,3}
    assume h({1, 2}, {3, 4, 5}) == {1,2}
