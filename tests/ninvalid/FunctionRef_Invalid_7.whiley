import * from whiley.lang.*

type Proc is ref {real(real) func}

method func(Proc this, int x) => int:
    return x + 1

method test(Proc this, real arg) => real:
    return this.func(arg)

function id(real x) => real:
    return x

method main(System.Console sys) => void:
    p = new {func: &id}
    x = p.test(123)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(12545)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(-11)
    sys.out.println("GOT: " + Any.toString(x))
