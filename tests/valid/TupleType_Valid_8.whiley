

constant GAME is [(0, 0), (1, 1), (0, 1), (2, 2), (0, 2), (2, 2)]

public export method test() -> void:
    assert GAME[0] == (0,0)
    assert GAME[1] == (1,1)
    assert GAME[2] == (0,1)
    assert GAME[3] == (2,2)
    assert GAME[4] == (0,2)
    assert GAME[5] == (2,2)
