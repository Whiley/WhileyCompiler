int f({int->int} dict):
    x = 0
    for x,y in dict:
        x = x + y
    return x
