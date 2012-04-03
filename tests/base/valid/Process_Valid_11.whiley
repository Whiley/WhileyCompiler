import println from whiley.lang.System

define MyProc1 as ref { int data }
define MyProc2 as ref { any data }

void MyProc1::set(int d):
    this->data = d

any MyProc2::get():
    return this->data

MyProc2 ::create(any data):
    return new {data: data}

void ::main(System.Console sys):
    p2 = create(1.23)
    p2.set(1)
    sys.out.println(Any.toString(p2.get()))

