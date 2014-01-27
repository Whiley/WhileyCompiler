import println from whiley.lang.System

type Proc is &{int data}

method read(Proc this, int x) => int:
    return x + 1

method test(Proc p, int arg) => int:
    return p.read(arg)

method main(System.Console sys) => void:
    p = new {data: 1}
    x = test(p, 123)
    sys.out.println("GOT: " ++ Any.toString(x))
    x = test(p, 12545)
    sys.out.println("GOT: " ++ Any.toString(x))
    x = test(p, -11)
    sys.out.println("GOT: " ++ Any.toString(x))
