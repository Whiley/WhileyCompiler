type OpenRecord is {int x, ...}

function getField(OpenRecord r) -> int:
    if r is {int x}:
        return 0
    else:
        if r is {int z, int y}:
            return 1
        else:
            return 2
