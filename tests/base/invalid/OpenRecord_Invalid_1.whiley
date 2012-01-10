import * from whiley.lang.*

define OpenRecord as {int field, ...}

int getField(OpenRecord r):
    return r.field

void ::main(System.Console sys, [string] args):
    r = {field: 1}
    sys.out.println(getField(r))
    r = {field: 2, x: "hello"}
    sys.out.println(getField(r))
    r = {x: 1, y: 2}
    sys.out.println(getField(r))
    
    
