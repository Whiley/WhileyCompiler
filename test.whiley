define expr as {int}|bool

void f(expr e):
    if e ~= bool:
        print "a"
