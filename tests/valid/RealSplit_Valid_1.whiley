

function f(real z) -> {int n, int d}:
    int x
    int y
    x/y = z
    return {n: x, d: y}

public export method test() :
    assume f(10.0 / 5.0) == {n: 2, d: 1}
    assume f(10.0 / 4.0) == {n: 5, d: 2}
    assume f(1.0 / 4.0) == {n: 1, d: 4}
    assume f(103.0 / 2.0) == {n: 103, d: 2}
    assume f(-10.0 / 5.0) == {n: -2, d: 1}
    assume f(-10.0 / 4.0) == {n: -5, d: 2}
    assume f(-1.0 / 4.0) == {n: -1, d: 4}
    assume f(-103.0 / 2.0) == {n: -103, d: 2}
    assume f(-10.0 / -5.0) == {n: 2, d: 1}
    assume f(-10.0 / -4.0) == {n: 5, d: 2}
    assume f(-1.0 / -4.0) == {n: 1, d: 4}
    assume f(-103.0 / -2.0) == {n: 103, d: 2}
    assume f(10.0 / -5.0) == {n: -2, d: 1}
    assume f(10.0 / -4.0) == {n: -5, d: 2}
    assume f(1.0 / -4.0) == {n: -1, d: 4}
    assume f(103.0 / -2.0) == {n: -103, d: 2}
