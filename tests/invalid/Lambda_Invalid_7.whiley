type fun_t<T> is function(T)->(int)

function id(fun_t<int> fn) -> fun_t<int|bool>:
    return fn