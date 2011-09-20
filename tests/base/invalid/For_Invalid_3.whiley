import * from whiley.lang.*

int f({int->int} dict):
    x = 0
    for y,x in dict:
        x = x + y
    return x
