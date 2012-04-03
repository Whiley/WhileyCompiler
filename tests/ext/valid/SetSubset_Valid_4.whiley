import println from whiley.lang.System

string f({int} xs, {int} ys) requires xs ⊂ ys:
    return "XS IS A SUBSET"

void ::main(System.Console sys):
    sys.out.println(f({1,2},{1,2,3}))
    sys.out.println(f({1},{1,2,3}))
