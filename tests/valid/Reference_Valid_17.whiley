public export method test():
    &(null|(int[])) c = new (null|(int[])) [1,2,3]
    &(null|(int[])) d = c
    *c = [4,5,6]
    assume (*c) == [4,5,6]
    assume (*d) == [4,5,6]
    *c = (null|(int[])) null
    assume (*c) == null
    assume (*d) == null

