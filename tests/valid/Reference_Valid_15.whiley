public export method test():
    &(null|int) c = new (null|int) 1
    &(null|int) d = c
    *c = 123
    assume (*c) == 123
    assume (*d) == 123
    *c = null
    assume (*c) == null
    assume (*d) == null

