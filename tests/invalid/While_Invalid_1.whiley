import * from whiley.lang.*

method main(System.Console sys) => void:
    int j = 0
    int i
    //
    while j < |sys.args|:
        i = 1
        j = j + 1
    debug Any.toString(i)
