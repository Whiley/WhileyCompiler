type OpenRecord is {int field, ...}

function getField(OpenRecord r) -> int:
    return r.field

method g() -> int
    OpenRecord r = {y: 2, x: 1}
    return getField(r)
