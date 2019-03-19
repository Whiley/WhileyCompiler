function f<S,T>(S x, T y) -> (T|S r)
ensures (x == r):
    //
    return x

public export method test():
    //
    assert f<int,int>(123,2) == 123
    assert f<int,bool>(123,false) == 123
    assert f<bool,int>(false,123) == false
    assert f<bool,bool>(false,true) == false
