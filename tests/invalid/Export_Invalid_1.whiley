// This method is an error because can only ever have one export
// method with a given name.

export int f(int x):
    return x+2

export int f([int] x):
    return |x|+2