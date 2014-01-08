
type i8 is int where ($ >= -128) && ($ <= 127)

function f(int x) => {i8}
requires (x == 0) || (x == 256):
    return {x}

method main(System.Console sys) => void:
    bytes = f(256)
    debug Any.toString(bytes)
