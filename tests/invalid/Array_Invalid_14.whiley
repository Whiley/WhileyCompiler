function f(int[] xs) -> (int r)
requires |xs| > 0
ensures r >= 0 && r <= |xs|:
    //
    return 0

function g(int x) -> (int r)
requires x >= 0 && x < 1:
    //
    return x
    
public export method test():
    //
    assume g(f([0])) == 0

