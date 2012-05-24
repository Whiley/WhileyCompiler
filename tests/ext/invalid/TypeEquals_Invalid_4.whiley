import println from whiley.lang.*

define pos as int where $ > 0

define rec1 as {
    any f1,
    pos f2
}

define rec2 as {
    int f1,
    int f2
}

define rec12 as rec1 | rec2

int f(rec12 x):
    return 1

public void ::main(System.Console console):
    r1 = {f1: "hello", f2: 2} // valid rec1
    r2 = {f1: 1, f2: 0}       // valid rec2
    r3 = {f1: "hello", f2: 0} // invalid rec1
    console.out.println(f(r1))
    console.out.println(f(r2))
    console.out.println(f(r3))