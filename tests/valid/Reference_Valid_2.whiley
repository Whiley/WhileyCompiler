

public export method test():
    &bool[] b = new [true, false, true]
    bool x = (*b)[1]
    assume x == false
