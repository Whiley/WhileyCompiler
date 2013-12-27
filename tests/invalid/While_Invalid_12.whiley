define nat as int where $ > 0

int f(int v) ensures $ >= 0:
    i = 0
    while i < 100 where i >= 0:
        i = i - 1
        if i == v:
            break
        i = i + 2
    return i

        
