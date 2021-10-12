method copy(&int p, &int q)
requires p != q
ensures old(*p) == *p
ensures *q == *p:
   *q = 1
