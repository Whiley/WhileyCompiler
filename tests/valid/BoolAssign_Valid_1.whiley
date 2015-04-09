import whiley.lang.*

method main(System.Console sys) -> void:
    bool x = true
    assert x == true
    x = false
    assert x == false
