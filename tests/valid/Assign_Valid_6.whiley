function update(int|null current, int mode) -> (int|null r):
    // Adapted from #950
    if current is int:
        current = null
    else:
        current = mode
    //
    return current

public export method test():
    assume update(null,123) == 123
    assume update(123,123) == null