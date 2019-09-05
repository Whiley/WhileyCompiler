type IntList is int | int[]

method test():
    IntList x = [1,2,3]
    // Following is safe
    assume x[0] == 1
    // Invalid x
    x = 1    
    // Following is unsafe
    assume x[0] == 1
    