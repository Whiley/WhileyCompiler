method f<&a,T>(&a:int x, T y) -> (int r):
    //
    return *x

public method get<&b>(int v, &b:int y) -> (int r):
    //
    &this:int x = new (6)
    //
    return f<this,int>(x,2) + f<b,int>(y,3)

public export method test():
    //
    &this:int p = new (123)
    //
    int v = get<this>(2,p)
    //
    assume v == 129
