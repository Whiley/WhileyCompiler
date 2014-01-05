import println from whiley.lang.System

type Proc is &{int(int) func}

method func(Proc this, int x) => int:
    return x + 1

method test(Proc this, int arg) => int:
    return *this.func(arg)

function id(int x) => int:
    return x

method main(System.Console sys) => void:
    p = new {func: &id}
    x = p.test(123)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(12545)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(-11)
    sys.out.println("GOT: " + Any.toString(x))
