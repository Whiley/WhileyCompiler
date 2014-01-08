
type i8 is int where ($ >= -128) && ($ <= 127)

function g(int x) => int
ensures ($ > 0) && ($ <= 256):
    if x <= 0:
        return 1
    else:
        return x

function f(int x) => {i8}:
    return {g(x)}

method main(System.Console sys) => void:
    bytes = f(256)
    debug Any.toString(bytes)
