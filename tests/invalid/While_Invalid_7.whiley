
type nat is int where $ >= 0

function extract([int] ls) => [nat]:
    i = 0
    r = []
    while i < |ls|:
        r = r + [ls[i]]
        i = i + 1
    return r

method main(System.Console sys) => void:
    rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    debug Any.toString(rs)
