import * from whiley.lang.*

define rec1 as { int x }
define rec2 as { rec1 current }

rec2 f(rec2 r):
    r.current.x = 1
    return r

void ::main(System.Console console):
    r = { current: { x: 0 } }
    console.out.println("BEFORE: " + r)
    console.out.println("AFTER: " + f(r))
