function sum(int[] xs) -> (int r)
// Every element in xs is non-negative
requires all { i in 0..|xs| | xs[i] >= 0 }
// Every element in xs is actually positive
requires all { i in 0..|xs| | xs[i] > 0 }
// result is positive
ensures r >= 0:
    //
    int i = 0
    int sum = 0
    //
    while i < |xs|
    where i >= 0 && sum >= 0:
        sum = sum + xs[i]
        i = i + 1
    //
    return sum

public export method test():
    assume sum([1;0]) == 0
    assume sum([1,2,3]) == 6