import * from whiley.lang.*

type Error is {string msg}

type WrongError is {int msg}

function f(int x) -> int throws WrongError:
    if x < 0:
        throw {msg: "error"}
    return 1
