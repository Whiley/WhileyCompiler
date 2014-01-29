import * from whiley.lang.*

method main(System.Console sys) => void:
    int i = 0
    //
    while i < |args|:
        r = r + |args[i]|
    //
    debug Any.toString(r)
