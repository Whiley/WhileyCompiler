import whiley.lang.System

type OpenRecord is {int x, ...}

function getField(OpenRecord r) -> ASCII.string:
    if r is {int x}:
        return "(1 field): " ++ Any.toString(r.x)
    else:
        if r is {int y, int x}:
            return "(2 fields): " ++ Any.toString(r.x) ++ ", " ++ Any.toString(r.y)
        else:
            return "(? fields): " ++ Any.toString(r.x) ++ ", ..."

method main(System.Console sys) -> void:
    OpenRecord r = {x: 1}
    sys.out.println_s(getField(r))
    r = {y: "hello", x: 2}
    sys.out.println_s(getField(r))
    r = {y: 1, x: 3}
    sys.out.println_s(getField(r))
    r = {z: 1, y: 1, x: 3}
    sys.out.println_s(getField(r))
