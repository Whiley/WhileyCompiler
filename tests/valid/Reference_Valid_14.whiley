public export method test():
    &(int[]) c = new [0,1,2]
    &(int[]) d = c
    (*c)[0] = 3
    (*c)[1] = 4
    (*c)[2] = 5    
    assume (*c) == [3,4,5]
    assume (*d) == [3,4,5]

