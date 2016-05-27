

type MyProc is &{bool flag}

method run(MyProc _this) -> bool:
    if _this->flag:
        return true
    else:
        return false

public export method test() :
    MyProc mproc = new {flag: false}
    assume run(mproc) == false
    mproc = new {flag: true}
    assume run(mproc) == true
