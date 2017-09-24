type OpenRecord1 is {int field, ...}
type OpenRecord2 is {int field, int other}

function getField(OpenRecord1 r) -> OpenRecord2:
    return r
