public export method test():
    int r = 0
    //
    for i in 0..5:
        r = r + i
    //
    assume r == (0+1+2+3+4)