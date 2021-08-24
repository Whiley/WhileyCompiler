method m<T>(T item, method(T,T)->(T) mn) -> (T r):
    //
    return mn(item,item)

method add(int x, int y) -> (int z):
    return x+y

public export method test():
    int x = m(123,&add)
    int y = m<int>(246,&add)
    assume x == 246
    assume y == 492

