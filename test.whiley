define pset as {int} where no { x in $ | x < 0 }

void f({int} ys):
    pset xs = ys