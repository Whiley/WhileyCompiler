import println from whiley.lang.System


define MyMeth as int::(int)

int ::read(int x):
    return x + 123

int ::test(MyMeth m):
    return m(1)

void ::main(System.Console sys):
    r = test(&read)
    sys.out.println(Any.toString(r))


