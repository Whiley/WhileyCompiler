import whiley.lang.*

type MyProc is &{int x}

method inc(MyProc this, int i) -> void:
    this->x = this->x + i

method main(System.Console sys) -> void:
    MyProc mproc = new {x: 1}
    inc(mproc, 10)
    sys.out.println(mproc->x)
