property inc(int[] xs) -> (int[] r, int q):
    (xs,0)

public export method test():
    assert inc([]) == ([],0)
    assert inc([1,2]) == ([1,2],0)    