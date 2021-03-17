method m(int[] xs) -> (int r)
requires xs[0] >= 0:
    return xs[0]

//
public export method test():
    int[] xs = [1,2,3]
    m(xs)
