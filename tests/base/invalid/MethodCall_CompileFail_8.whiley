

int f():
    x = spawn 1
    return 1

void ::main(System.Console sys):
    x = f()
    sys.out.println(Any.toString(x))
