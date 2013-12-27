import println from whiley.lang.System
import print from whiley.lang.System

define MyObject as ref { System.Console sys }

void MyObject::f(int x):
    this->sys.out.println(Any.toString(x))

void ::main(System.Console sys):
    m = new { sys: sys }
    m.f(1)
    sys.out.print("")
