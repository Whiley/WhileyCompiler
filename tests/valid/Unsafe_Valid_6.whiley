type uint is (int x) where x >= 0

unsafe uint x = -1

unsafe public export method test():
    assert x == -1