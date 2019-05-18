method assign<T>(&T x, &T y, T v1, T v2)
ensures *x == v1
ensures *y == v2:
    // Broken if x == y
    *x = v1
    *y = v2

