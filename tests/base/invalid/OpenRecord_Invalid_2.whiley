

define OpenRecord as {int x, ...}

string getField(OpenRecord r):
    if r is {int x}:
        return "(1 field): " + r.x
    else if r is {int y,int z}:
        return "(2 fields): " + r.x + ", " + r.y
    else:
        return "(? fields): " + r.x + ", ..."

void ::main(System.Console sys):
    r = {x: 1}
    sys.out.println(getField(r))
    r = {x: 2, y: "hello"}
    sys.out.println(getField(r))
    r = {x: 3, y: 1}
    sys.out.println(getField(r))
    r = {x: 3, y: 1, z:1}
    sys.out.println(getField(r))
    
    
