import whiley.lang.*

type OpenRecord is {int x, ...}

function getField(OpenRecord r) -> int:
    if r is {int x}:
        return r.x
    else:
        if r is {int y, int x}:
            return r.x + r.y
        else:
            return -r.x

method main(System.Console sys) -> void:
    OpenRecord r = {x: 1}
    sys.out.println(getField(r))
    r = {y: 1, x: 3}
    sys.out.println(getField(r))
    r = {z: 1, y: 1, x: 3}
    sys.out.println(getField(r))
    r = {y: "hello", x: 2}
    sys.out.println(getField(r))
    
