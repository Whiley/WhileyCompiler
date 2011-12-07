import * from whiley.lang.*

void ::main(System sys,[string] args):
    i=0
    r=0
    while i < |args| where j > 0:
        r = r + |args[i]|
    debug Any.toString(r)
