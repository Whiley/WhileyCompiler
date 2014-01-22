
type c4nat is int where $ < 10

function h() => int
ensures $ <= 5:
    return 5

function f() => c4nat:
    return h() * 2

method main(System.Console sys) => void:
    debug Any.toString(f())
