import * from whiley.lang.*

method main(System.Console sys) => void:
    int i = 0
    int r
    //
    while i < |sys.args|:
        r = r + |sys.args[i]|
    //
    debug Any.toString(r)
