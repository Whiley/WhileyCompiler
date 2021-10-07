public export method test():
    &(null|(int[])) c = new (null|(int[])) [1,2,3]
    &(null|(int[])) d = c
    assert (*c) == [1,2,3]
    assert (*d) == [1,2,3]
    *c = (null|(int[])) null
    assert (*c) == null
    assert (*d) == null

