type fun_t<T> is function(T)->(int)

function inc(int x) -> (int y)
ensures y == x + 1:
    //
    return x+1

function id<T>(T x) -> T:
    return x

method test():
    // following is broken!!
    fun_t<int|bool> fn = id(&inc)
    // because it allows following
    assume fn(false) == 0
    