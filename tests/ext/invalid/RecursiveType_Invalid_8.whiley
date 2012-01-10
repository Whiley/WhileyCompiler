import * from whiley.lang.*

define neg as int where $ < 0
define pos as int where $ > 0

define exp1 as neg | {exp1 rest}
define exp2 as pos | {exp2 rest}


exp2 f(exp1 e1):
    return e1

void ::main(System.Console sys,[string] args):
    x = f(-1)
    debug Any.toString(x)
