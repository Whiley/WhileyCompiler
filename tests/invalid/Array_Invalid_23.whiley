function f(int[] xs) -> (int r)
requires |xs| > 0
ensures r < |xs|:
    //
    return 0

function g(int x) -> (int r)
requires x >= 0:
    //
    return x
    
public export method test():
    //
    assume g(f([0])) == 0

