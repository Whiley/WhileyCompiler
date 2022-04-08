type fun_t is method(int)->(int)

function alert() -> fun_t:
    return &(int x -> drop(x))


method drop(int x):
    skip