define MyProc as process { bool flag }

void MyProc::run():
    if flag:
        print "TRUE"
    else:
        print "FALSE"

void System::main([string] args):
    mproc = spawn { flag:false }     
    mproc->run()
