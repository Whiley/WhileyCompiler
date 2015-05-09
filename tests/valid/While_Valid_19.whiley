

constant SIZE is 5

public export method test() -> void:
    [{int}] components = []
    while |components| < SIZE:
        components = components ++ [{}]
    assume components == [{},{},{},{},{}]
