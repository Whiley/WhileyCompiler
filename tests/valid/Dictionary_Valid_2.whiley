

function f(int x) -> {int=>int}:
    return {1=>x, 3=>2}

function get(int i, {int=>int} map) -> int:
    return map[i]

public export method test() -> void:
    assume get(1, f(1)) == 1
    assume get(1, f(2)) == 2
    assume get(1, f(3)) == 3
    assume get(3, f(3)) == 2
