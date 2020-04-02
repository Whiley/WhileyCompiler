type handler_t<T> is function(T)->(T,T)

function apply<T>(T k, handler_t<T> h) -> (T a, T b):
    return h(k)

function dup(int x) -> (int a, int b)
ensures (a == x) && (a == b):
    return (x,x)

public export method test():
    assume apply(1,&dup) == (1,1)
    assume apply(2,&dup) == (2,2)    
