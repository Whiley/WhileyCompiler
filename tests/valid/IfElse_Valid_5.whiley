

type Record is { int flag }

function getFlag(Record d) -> int:
    if d.flag >= 0:
        int r = 1
        if d.flag > 0:
            return r
    else:
        int r = 0
        return 0
    //
    return -1

public export method test():
    Record r = {flag: 1}
    assume getFlag(r) == 1
    r = {flag: 0}
    assume getFlag(r) == -1
    r = {flag: -1}
    assume getFlag(r) == 0  
