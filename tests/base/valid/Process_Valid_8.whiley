define MyProc as process { bool flag }

void MyProc::run(System sys):
    if flag:
        sys.this.out.println("TRUE")
    else:
        sys.this.out.println("FALSE")

void System::main([string] args):
    mproc = spawn { flag:false }     
    mproc.run(this)
