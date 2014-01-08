
type nat is int where $ >= 0

method main(System.Console sys) => void:
    xs = [1, 2, 3]
    r = |sys.args| - 1
    for x in xs where r >= 0:
        r = r + x
    debug Any.toString(r)
