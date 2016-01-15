

type MyProc is &{bool flag}

method run(MyProc this) -> bool:
    if this->flag:
        return true
    else:
        return false

public export method test() :
    MyProc mproc = new {flag: false}
    assume run(mproc) == false
    mproc = new {flag: true}
    assume run(mproc) == true
