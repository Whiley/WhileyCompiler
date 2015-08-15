

public export method test():
    &real[] b = new [1.0, 2.0, 3.0]
    real x = (*b)[1]
    assume x == 2.0
