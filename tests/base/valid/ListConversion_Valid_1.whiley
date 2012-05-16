import println from whiley.lang.System

string f([real] ls):
    return Any.toString(ls)

void ::main(System.Console sys):
    sys.out.println(f([1,2,3]))
