import println from whiley.lang.System

type Reader is {int ::(int) read}

method f(int amount) => int:
    return 1

method m(Reader r, int x) => int:
    return r.read(x)

method main(System.Console sys) => void:
    reader = {read: &f}
    data = m(reader, 1)
    sys.out.println(Any.toString(data))
