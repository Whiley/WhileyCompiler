import println from whiley.lang.System

define MyProc as ref { bool flag }

void MyProc::run(System.Console sys):
    if this->flag:
        sys.out.println("TRUE")
    else:
        sys.out.println("FALSE")

void ::main(System.Console sys):
    mproc = new { flag:false }     
    mproc.run(sys)
