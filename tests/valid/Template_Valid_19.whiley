function select<S,T>(S|T x, S y) -> (S|T r):
    if x == y:
        return y
    else:
        return x

public export method test():
    int|bool a1 = (int|bool) 1
    int|bool bt = (int|bool) true
    bool|int c2 = (bool|int) 2
    bool|int df = (bool|int) false
    //
    assume select(a1,1) == 1
    assume select<int,bool>(a1,2) == 1
    //
    assume select(bt,true) == true
    assume select<bool,int>(bt,false) == true
    //
    assume select(c2,2) == 2
    assume select<bool,int>(c2,false) == 2
    //
    assume select(df,2) == false
    assume select<int,bool>(df,1) == false
    