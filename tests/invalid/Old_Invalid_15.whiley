method copy(&int p, &int q)
ensures *p == 1
ensures *q == 2:
   *q = 2
   *p = 1
