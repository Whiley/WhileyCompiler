type t is method(int)->()
type s is method(int)->()|method(bool)->()

public export method test(s x):
    t m = &test
