



public export method test() -> void:
    int i = 0
    do:
        if i == 2:
            break
        i = i + 1
    while i < 5
    assume i == 2
