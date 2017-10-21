// Test final parameter
function id(final int x) -> (int r)
ensures r == x:
    return x

public export method test():
    assert id(-1) == -1
    assert id(0) == 0
    assert id(1) == 1