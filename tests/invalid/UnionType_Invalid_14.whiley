type rec_t is {int x, int y}|{int x, bool y}

function f(rec_t r) -> int:
    r.y = 1
    return r.x