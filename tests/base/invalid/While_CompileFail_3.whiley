import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    i=0
    while i < |args|:
        r = r + |args[i]|
    debug Any.toString(r)
