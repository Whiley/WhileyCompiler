import * from whiley.lang.*

method main(System.Console sys) => void:
    int j = 0
    int i
    //
    while j < |args|:
        i = 1
        j = j + 1
    debug Any.toString(i)
