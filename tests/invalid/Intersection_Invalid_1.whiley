type EmptyList is [int] & [real]

function size(EmptyList l) -> int:
    return |l|

function f([int] x) -> int:
    return size(x)
