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
    r = 1
    for i in 0 .. exponent:
        r = r * base
    return r
    
// round an arbitrary number x to the largest integer
// not greater than x 
int floor(real x):
	num,den = x
	r = num / den  
	if x < 0: 	 
    	return r - 1 
    return r 

    
// round an arbitrary number x to the smallest integer
// not smaller than x 
int ceil(real x):
	num,den = x
	r = num / den  
	if x > 0: 	 
    	return r + 1 
    return r 
    
// round an arbitrary number to the nearest decimal
int round(real x):
	num,den = x
    return num