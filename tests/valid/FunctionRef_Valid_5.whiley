import whiley.lang.*

type Func is {
    function read(int)->int
}

function id(int x) -> int:
    return x

function test(Func f, int arg) -> int:
    return f.read(arg)

method main(System.Console sys) -> void:
    int x = test({read: &id}, 123)
    sys.out.println_s("GOT: " ++ Any.toString(x))
    x = test({read: &id}, 12545)
    sys.out.println_s("GOT: " ++ Any.toString(x))
    x = test({read: &id}, -11)
    sys.out.println_s("GOT: " ++ Any.toString(x))
