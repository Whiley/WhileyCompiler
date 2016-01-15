

function f(int x, int y) -> {int nx, int ny}
requires x == (2 * y):
    x = x + 2
    y = y + 1
    assert (2 * y) == x
    return {nx: x, ny: y}

public export method test() :
    assume f(2, 1) == {nx: 4, ny: 2}
