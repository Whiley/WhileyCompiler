import * from whiley.lang.*

define MyProc as process { bool flag }

void MyProc::run(System.Console sys):
    if this->flag:
        sys.out.println("TRUE")
    else:
        sys.out.println("FALSE")

void ::main(System.Console sys):
    mproc = spawn { flag:false }     
    mproc.run(sys)
