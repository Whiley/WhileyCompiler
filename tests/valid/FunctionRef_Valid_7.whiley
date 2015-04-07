import whiley.lang.*

type Proc is &{int data}

method read(Proc this, int x) -> int:
    return x + 1

method test(Proc p, int arg) -> int:
    return read(p,arg)

method main(System.Console sys) -> void:
    Proc p = new {data: 1}
    int x = test(p, 123)
    sys.out.println_s("GOT: " ++ Any.toString(x))
    x = test(p, 12545)
    sys.out.println_s("GOT: " ++ Any.toString(x))
    x = test(p, -11)
    sys.out.println_s("GOT: " ++ Any.toString(x))
