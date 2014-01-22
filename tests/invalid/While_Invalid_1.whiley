import * from whiley.lang.*

method main(System.Console sys) => void:
    j = 0
    while j < |args|:
        i = 1
        j = j + 1
    debug Any.toString(i)
