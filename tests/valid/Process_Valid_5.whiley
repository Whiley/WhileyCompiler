import whiley.lang.System

type MyProc is &{bool flag}

method run(MyProc this, System.Console sys) => void:
    if this->flag:
        sys.out.println("TRUE")
    else:
        sys.out.println("FALSE")

method main(System.Console sys) => void:
    MyProc mproc = new {flag: false}
    run(mproc, sys)
