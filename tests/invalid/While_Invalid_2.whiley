import * from whiley.lang.*

method main(System.Console sys) -> void:
    int i = 0
    int r = 0
    //
    while i < |sys.args| where j > 0:
        r = r + |sys.args[i]|
    //
    debug Any.toString(r)
