property inc(int x) -> (int r):
    x + 1

public export method test():
    assert 1 < inc(1) 
    assert inc(0) == 1