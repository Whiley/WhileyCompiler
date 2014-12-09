type Record is { int tag }

function Record(int tag) -> Record:
    Record r
    r.tag = tag
    return r
