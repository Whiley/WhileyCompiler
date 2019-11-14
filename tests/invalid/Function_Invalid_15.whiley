type fn_t is function()->()

function g() -> int:
    fn_t fn = &f
    return fn()
    
function f():
    return h()

function h()->int:
    return 0