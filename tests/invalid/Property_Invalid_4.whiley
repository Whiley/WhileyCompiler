property contains(int[] xs, int x)
where some { i in 0..|xs| | xs[i] == x }

function id(int[] xs, int x, int y) -> (int[] ys)
requires contains(xs,y)
ensures contains(ys,x):
    return xs

