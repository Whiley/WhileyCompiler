

public export method test() :
    int x = 112233445566778899
    assert x == 112233445566778899
    x = x + 1
    assert x == 112233445566778900
    x = x - 556
    assert x == 112233445566778344
    x = x * 123
    assert x == 13804713804713736312
    x = x / 2
    assert x == 6902356902356868156
