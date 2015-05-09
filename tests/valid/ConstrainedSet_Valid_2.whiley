

function f(int x) -> {int}:
    return {x}

public export method test() -> void:
    {int} bytes = f(0)
    assume bytes == {0}
