import * from whiley.lang.*

void ::main(System.Console sys):
    i=0
    while i < |args|:
        r = r + |args[i]|
    debug Any.toString(r)
