import * from whiley.lang.*

method main(System.Console sys) => void:
    i = 0
    while i < |args|:
        r = r + |args[i]|
    debug Any.toString(r)
