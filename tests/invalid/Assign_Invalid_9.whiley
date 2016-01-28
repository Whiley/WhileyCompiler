function f(int[] xs) -> int:
    if |xs| == 0:
        return 0
    else:
        xs[0] = false
        return 1
