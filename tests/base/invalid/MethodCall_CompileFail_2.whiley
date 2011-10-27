import * from whiley.lang.*

define dummy as process {int x}

void dummy::f(int x):
    sys.out.println(toString(x))

void ::main(System sys,[string] args):
    this.f(1)
