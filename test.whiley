define expr as int|[int]

int f(expr e):
    if e ~= int:
        return e
    else:
        return |e| 
