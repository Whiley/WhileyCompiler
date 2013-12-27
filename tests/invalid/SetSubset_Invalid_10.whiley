

void f({int} xs, {int} ys) requires xs ⊂ ys:
    debug "XS IS A SUBSET"

void g({int} xs, {int} ys):
    f(xs,ys)

void ::main(System.Console sys):
    g({1,2,3},{1,2,3})