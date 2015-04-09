import whiley.lang.*

function pred() -> bool:
    return false

method main(System.Console sys) -> void:
    assume !pred()

