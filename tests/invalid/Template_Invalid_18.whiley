type NonEmptyArray<T> is (T[] arr) where |arr| > 0

public export method test():
    NonEmptyArray<int> a1 = []
