import * from whiley.lang.*

define MyProc1 as process { int data }
define MyProc2 as process { any data }

void MyProc2::set(any d):
    this.data = d

int MyProc1::get():
    return this.data

MyProc1 System::create(int data):
    return spawn {data: data}

void ::main(System.Console sys):
    p2 = this.create(1)
    p2.set(1.23)
    sys.out.println(Any.toString(p2.get()))

