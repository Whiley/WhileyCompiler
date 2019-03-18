function fun<T>(T x) -> (T y):
    return x

function fun<S,T>(S x, T y) -> (T z):
    return y

method consume<S>(S x):
    skip // what else could I do?

public method test():
    consume(&fun)