import println from whiley.lang.System

define OpenRecord as {int field, ...}

int getField(OpenRecord r):
    return r.field

void ::main(System.Console sys):
    r = {field: 1}
    sys.out.println(getField(r))
    r = {field: 2, x: "hello"}
    sys.out.println(getField(r))
    r = {field: 3, x: 1, y: 2}
    sys.out.println(getField(r))
    
    
