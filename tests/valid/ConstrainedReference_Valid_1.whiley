

type nat is (int x) where x >= 0

method f(&int v) -> (int r)
ensures r >= 0:
    //
    if v is &nat:
        return (*v) + 1
    //
    return 0

public export method test():
    assume f(new 1) == 2
    assume f(new -1) == 0
