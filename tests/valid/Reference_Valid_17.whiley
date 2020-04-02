public export method test():
    &(null|(int[])) c = new (null|(int[])) [1,2,3]
    &(null|(int[])) d = c
    assume (*c) == [1,2,3]
    assume (*d) == [1,2,3]
    *c = (null|(int[])) null
    assume (*c) == null
    assume (*d) == null

