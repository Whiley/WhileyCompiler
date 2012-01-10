import * from whiley.lang.*

define dummy as process {int x}

void dummy::f(int x):
    sys.out.println(Any.toString(x))

void ::main(System.Console sys):
    this.f(1)
