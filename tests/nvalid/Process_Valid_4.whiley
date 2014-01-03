import println from whiley.lang.System

type MyProc is ref {int x}

method inc(MyProc this, int i) => void:
    this->x = this->x + i

method main(System.Console sys) => void:
    mproc = new {x: 1}
    mproc.inc(10)
    sys.out.println(Any.toString(mproc->x))
