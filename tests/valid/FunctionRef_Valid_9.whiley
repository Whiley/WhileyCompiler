import whiley.lang.*

type Proc is &{
    function func(int) -> int
}

method func(Proc this, int x) -> int:
    return x + 1

method test(Proc this, int arg) -> int:
    return (*this).func(arg)

function id(int x) -> int:
    return x

method main(System.Console sys) -> void:
    Proc p = new {func: &id}
    int x = test(p, 123)
    sys.out.println_s("GOT: " ++ Any.toString(x))
    x = test(p, 12545)
    sys.out.println_s("GOT: " ++ Any.toString(x))
    x = test(p,-11)
    sys.out.println_s("GOT: " ++ Any.toString(x))
