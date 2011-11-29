package whiley.lang

// Increase up to a given size
public [int] enlarge([int] list, int size, int element):
    while |list| <= size:
        list = list + [element]
    return list

// create a list of a given size with the given element
public [int] create(int size, int element) requires size >= 0, ensures |$| == size:
    r = []
    i = 0
    while i < size:
        r = r + [element]
        i = i + 1
    return r 

public [bool] create(int size, bool element) requires size >= 0, ensures |$| == size:
    r = []
    i = 0
    while i < size:
        r = r + [element]
        i = i + 1
    return r 

public [bool] reverse([bool] list):
    return list[|list|..0]    

public [byte] reverse([byte] list):
    return list[|list|..0]    

public [int] reverse([int] list):
    return list[|list|..0]    

