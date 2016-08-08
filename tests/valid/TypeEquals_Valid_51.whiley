function f(bool|int|null x) -> (int|bool r):
    //
    if x is int || x is bool:
        return x
    else:
        return 0

public export method test():
    assume f(0) == 0
    assume f(1) == 1
    assume f(2) == 2
    assume f(false) == false
    assume f(true) == true 
    assume f(null) == 0