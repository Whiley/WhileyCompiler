



public export method test() :
    int i = 0
    while i < 5:
        if i == 3:
            break
        i = i + 1
    assume i == 3
