import whiley.lang.*

type MyProc is &{bool flag}

method run(MyProc this) -> bool:
    if this->flag:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    MyProc mproc = new {flag: false}
    assert run(mproc) == false
    mproc = new {flag: true}
    assert run(mproc) == true
