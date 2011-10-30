package whiley.lang

// return absolute value
int abs(int x):
    if x < 0:
        return -x
    else:
        return x

real abs(real x):
    if x < 0:
        return -x
    else:
        return x

// return larger of two values
int max(int a, int b):
    if a < b:
        return b
    else:
        return a

real max(real a, real b):
    if a < b:
        return b
    else:
        return a

// return small of two values
int min(int a, int b):
    if a > b:
        return b
    else:
        return a

real min(real a, real b):
    if a > b:
        return b
    else:
        return a

// not sure what to do with negative exponents
int pow(int base, int exponent) requires exponent > 0:
    for i in 0..exponent:
        base = base * base
    return base