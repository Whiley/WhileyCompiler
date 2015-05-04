

public export method test() -> void:
    bool x = true
    bool y = false
    assert (x && x) == true
    assert (x && y) == false
    assert (y && x) == false
    assert (y && y) == false        
    assert !y
    assert !!x
