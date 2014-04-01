
constant nat is {1, 2, 3, 4, 5}

function h() => (int x)
ensures x <= 3:
    //
    return 0

function f() => nat:
    return h()

method main(System.Console sys) => void:
    debug Any.toString(f())
