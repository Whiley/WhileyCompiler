import * from whiley.lang.*

type dummy is ref {int x}

method f(dummy this, int x) => void:
    sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    this.f(1)
