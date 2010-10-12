define MyProc as process { bool flag }

void MyProc::run(System sys):
    if flag:
        sys->out->println("TRUE")
    else:
        sys->out->println("FALSE")

void System::main([string] args):
    mproc = spawn { flag:false }     
    mproc->run(this)
