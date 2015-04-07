import whiley.lang.*

type MyProc is &{bool flag}

method run(MyProc this, System.Console sys) -> void:
    if this->flag:
        sys.out.println_s("TRUE")
    else:
        sys.out.println_s("FALSE")

method main(System.Console sys) -> void:
    MyProc mproc = new {flag: false}
    run(mproc, sys)
