import whiley.lang.*
import print from whiley.lang.*

type MyObject is &{System.Console sys}

method f(MyObject this, int x) -> void:
    this->sys.out.println(x)

method main(System.Console sys) -> void:
    MyObject m = new {sys: sys}
    f(m,1)
    sys.out.print_s("")
