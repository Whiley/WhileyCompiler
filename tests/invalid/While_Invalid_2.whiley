import * from whiley.lang.*

method main(System.Console sys) => void:
    int i = 0
    int r = 0
    //
    while i < |args| where j > 0:
        r = r + |args[i]|
    //
    debug Any.toString(r)
