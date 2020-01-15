type fun_t<T> is function(T)->(int)

function f<T>() -> fun_t<T>:
    return &(T x -> 0)

method test():
    // following is broken!!
    fun_t<int|bool> fn = f()
    // because it allows following
    assume fn(false) == 0
    