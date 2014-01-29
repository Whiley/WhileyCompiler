import * from whiley.lang.*

method main(System.Console sys) => void:
    int i = 0
    //
    while i < |sys.args|:
        r = r + |sys.args[i]|
    //
    debug Any.toString(r)
