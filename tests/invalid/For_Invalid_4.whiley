public export method test():
    int r
    //
    for i in 0..5 where r >= 0:
        r = r + i
    //
    assume r == (0+1+2+3+4)