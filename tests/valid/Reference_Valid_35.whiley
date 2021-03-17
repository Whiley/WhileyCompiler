method next(&int r):
    skip

method main():
    &int p = new 0
    &int q = new 1
    assert *q == 1
    next(p)
    assert *q == 1
