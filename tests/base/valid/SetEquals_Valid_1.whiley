import * from whiley.lang.*

string f({int} xs, {real} ys):
    if xs == ys:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void ::g(System.Console sys, {int} xs, {real} ys):
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
    sys.out.println(f(xs,ys))

void ::main(System.Console sys,[string] args):
    g(sys,{1,4},{1.0,4.0})
    g(sys,{1,4,4},{1.0,4.0})
    g(sys,{1,4},{1.0,4.2})
    g(sys,{1,4},{1.0,4.2})
    g(sys,{},{})
