

type EmptyList is [int] & [real]

function size(EmptyList l) -> int:
    return |l|

public export method test() -> void:
    assume size([]) == 0
