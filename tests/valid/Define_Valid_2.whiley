

type dr2point is {bool y, bool x}

public export method test() :
    dr2point p = {y: false, x: true}
    assert p == {x:true, y:false}

