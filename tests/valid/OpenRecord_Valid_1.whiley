

type OpenRecord is {int field, ...}

function getField(OpenRecord r) -> int:
    return r.field

public export method test() :
    OpenRecord r = {field: 1}
    assume getField(r) == 1
    r = {field: 2, x: "hello"}
    assume getField(r) == 2
    r = {field: 3, y: 2, x: 1}
    assume getField(r) == 3
