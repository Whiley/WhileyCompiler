import whiley.lang.*

constant ITEMS is [-1, 2, 3]

method main(System.Console sys) -> void:
    assert ITEMS == [-1,2,3]
