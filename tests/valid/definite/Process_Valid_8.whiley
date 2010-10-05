define MyProc as process { bool flag }

void MyProc::run():
    if flag:
        out->println("TRUE")
    else:
        out->println("FALSE")

void System::main([string] args):
    mproc = spawn { flag:false }     
    mproc->run()
