
function f([byte] data, int pos) -> int:
    method = data[pos]
    pos = pos + 1
    switch method:
        case 0:
            return 1
        case 1:
            return 0
    return 2
