

type MyProc is &{bool flag}

method run(MyProc this) -> bool:
    if this->flag:
        return true
    else:
        return false

public export method test() -> void:
    MyProc mproc = new {flag: false}
    assert run(mproc) == false
    mproc = new {flag: true}
    assert run(mproc) == true
