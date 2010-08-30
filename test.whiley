define pset as {int} where no { x in $ | x < 0 }

void f(pset ys):
    pset xs = ys
