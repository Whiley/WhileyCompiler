

type OpenRecord is {int x, ...}

function getField(OpenRecord r) -> int:
    if r is {int x}:
        return r.x
    else:
        if r is {int y, int x}:
            return r.x + r.y
        else:
            return -r.x

public export method test() :
    OpenRecord r = {x: 1}
    assume getField(r) == 1
    r = {y: 1, x: 3}
    assume getField(r) == 4
    r = {z: 1, y: 1, x: 3}
    assume getField(r) == -3
    r = {y: "hello", x: 2}
    assume getField(r) == -2
    
