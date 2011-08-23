import whiley.lang.*:*

define MyProc as process { bool flag }

void MyProc::run(System sys):
    if this.flag:
        sys.out.println("TRUE")
    else:
        sys.out.println("FALSE")

void ::main(System sys,[string] args):
    mproc = spawn { flag:false }     
    mproc.run(sys)
