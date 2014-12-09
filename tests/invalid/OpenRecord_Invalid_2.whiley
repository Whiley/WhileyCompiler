import * from whiley.lang.*

type OpenRecord is {int x, ...}

function getField(OpenRecord r) -> string:
    if r is {int x}:
        return "(1 field): " ++ r.x
    else:
        if r is {int z, int y}:
            return (("(2 fields): " ++ r.x) ++ ", ") + r.y
        else:
            return ("(? fields): " ++ r.x) ++ ", ..."

method main(System.Console sys) -> void:
    r = {x: 1}
    sys.out.println(getField(r))
    r = {y: "hello", x: 2}
    sys.out.println(getField(r))
    r = {y: 1, x: 3}
    sys.out.println(getField(r))
    r = {z: 1, y: 1, x: 3}
    sys.out.println(getField(r))
