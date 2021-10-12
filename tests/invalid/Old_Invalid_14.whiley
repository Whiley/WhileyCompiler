method copy(&int p, &int q)
ensures old(*p) == *p:
   *q = 1
