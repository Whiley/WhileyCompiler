public export method test():
    int i = 0
    int j = 0
    while i < 5:
        i = i + 1
        if i == 3:
            continue
        j = j + i
    assume j == 12
