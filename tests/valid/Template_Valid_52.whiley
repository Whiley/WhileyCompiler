function f<T>() -> (int|T x,int|T y):
    return (1,2)

public export method test():
    (int|bool a, int b) = f()
    //
    assume a == 1
    assume b == 2
    