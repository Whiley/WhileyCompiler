

public export method test() -> void:
    [int] x = "abcdefghjkl"
    [int] y = x[0..2]
    assert y == "ab"
    y = x[1..3]
    assert y == "bc" 
    y = x[2..|x|]
    assert y == "cdefghjkl"
