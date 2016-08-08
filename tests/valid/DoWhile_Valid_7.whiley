public export method test():
    int i = 0
    int j = 0
    do:
        i = i + 1
        if i == 3:
            continue
        j = j + i
    while i < 5
    assume j == 12
