import whiley.lang.*:*

string f({int} xs, {int} ys) requires xs ⊆ ys:
    return "XS IS A SUBSET"

string g({int} xs, {int} ys) requires xs ⊂ ys:
    return f(xs,ys)

void ::main(System sys,[string] args):
    sys.out.println(g({1,2},{1,2,3}))
    sys.out.println(g({1},{1,2,3}))
