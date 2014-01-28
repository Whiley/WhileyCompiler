import whiley.lang.System
import print from whiley.lang.System

type MyObject is &{System.Console sys}

method f(MyObject this, int x) => void:
    this->sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    m = new {sys: sys}
    m.f(1)
    sys.out.print("")
