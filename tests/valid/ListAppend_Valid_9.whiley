

public export method test() -> void:
    [int] l = [1, 2, 3]
    [real] r = [4.23, 5.5]
    r = r ++ (([real]) l)
    assert r == [4.23,5.5,1.0,2.0,3.0]
