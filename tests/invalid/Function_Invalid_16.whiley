type fn_t is function()->(int)

function g() -> int:
    return f(&h)
    
function f(fn_t fn):
    return fn()

function h()->int:
    return 0