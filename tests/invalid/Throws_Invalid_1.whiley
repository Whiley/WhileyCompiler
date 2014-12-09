import * from whiley.lang.*

type Error is {string msg}

function f(int x) -> int:
    if x < 0:
        throw {msg: "error"}
    return 1
