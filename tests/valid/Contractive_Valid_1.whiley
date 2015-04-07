import whiley.lang.*

type Contractive is Contractive | null

function f(Contractive x) -> Contractive:
    return x

method main(System.Console sys) -> void:
    x = f(null)
    sys.out.println(x)
