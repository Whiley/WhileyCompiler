public export method test(bool j):
    int r = 0
    //
    for i in 0..j:
        r = r + i
    //
    assume r == (0+1+2+3+4)