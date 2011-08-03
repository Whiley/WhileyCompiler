define MyProc as process { int data }

int MyProc::copy(MyProc p):
    p.data = this.data // NOT OK

int MyProc::get():
    return this.data

MyProc System::create(int data):
    return spawn {data: data}

void System::main([string] args):
    p1 = this.create(1)
    p2 = this.create(2)
    p1.copy(p2)
    out.println(str(p1.get()))
    out.println(str(p2.get()))

