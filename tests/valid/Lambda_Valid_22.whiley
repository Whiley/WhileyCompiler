function to_int(int|bool x) -> (int r):
    if x is int:
        return x
    else if x:
        return 1
    else:
        return 0

public export method test():
    function(int)->(int) fn = &to_int
    assume fn(1) == 1
