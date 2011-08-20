import whiley.lang.*:*

define Error as {string msg}

int f(int x):
    if x < 0:
        throw {msg: "error"}
    return 1
        
