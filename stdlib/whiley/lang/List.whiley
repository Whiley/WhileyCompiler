package whiley.lang

// Increase up to a given size
public [int] enlarge([int] list, int size, int element):
    while |list| <= size:
        list = list + [element]
    return list

// create a list of a given size with the given element
public [int] create(int size, int element):
    r = []
    i = 0
    while i < size:
        r = r + [element]
        i = i + 1
    return r 
