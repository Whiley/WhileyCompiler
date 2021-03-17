function f(int[] xs) -> (int r)
requires xs[0] == 0:
    //
    int n = 0
    while n < 10 where n >= 0 && xs[0] == 0:
        assert |xs| > 0
        xs[0] = 0
        n = n + 1
    //
    return n
        

public export method test():
    int[] xs = [0,0,0]
    assume f(xs) == 10 
