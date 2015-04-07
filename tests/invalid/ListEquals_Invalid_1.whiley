
function f([int] xs) -> bool
requires xs != []:
    return true

method main() -> void:
    f([1, 4])
    f([])
