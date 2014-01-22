
type pos is int where $ > 0

type neg is int where $ < 0

type expr is pos | neg

function g(neg x) => void:
    debug "NEGATIVE: " + Any.toString(x)

function f(expr e) => void:
    if e is pos:
        g(e)

method main(System.Console sys) => void:
    f(-1)
    f(1)
