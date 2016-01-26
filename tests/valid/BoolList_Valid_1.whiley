

public export method test() :
    bool[] ls = [true, false, true]
    assert ls == [true, false, true]
    assert ls[0] == true
    assert ls[1] == false
    assert ls[2] == true    
    ls[0] = false
    assert ls[0] == false
    assert ls[1] == false
    assert ls[2] == true    
