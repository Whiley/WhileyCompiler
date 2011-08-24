import whiley.lang.*:*

void f({int} xs, {int} ys) requires xs ⊂ ys:
    debug "XS IS A SUBSET"

void g({int} xs, {int} ys) requires xs ⊆ ys:
    f(xs,ys)

void ::main(System sys,[string] args):
    g({1,2,3},{1,2,3})
    g({1},{1,2,3})