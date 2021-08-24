function f<T>(T item, function(T,T)->(T) fn) -> (T r):
    //
    fn(item,item)
    //
    return item

function add(int x, int y) -> (int z):
    return x+y

public export method test():
    assume f(123,&add) == 123
    assume f<int>(246,&add) == 246

