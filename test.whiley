define un_t as [int]|int

int f(un_t x):
    if x ~= [int] && |x| > 0:
        return 1
    return 0
