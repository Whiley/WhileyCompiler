// This is something of a verifier workout.  Its trying to trigger a
// matching loop.
property equals(int[] xs, int[] ys)
where |xs| == |ys|
where all { i in 0..|xs| | xs[i] == ys[i] }

property sorted_a(int[] xs)
where |xs| == 0 || all { i in 1 .. |xs| | xs[i-1] < xs[i] }

property sorted_b(int[] xs)
where |xs| == 0 || all { i in 0 .. |xs|-1 | xs[i] < xs[i+1] }

property consecutive_a(int[] xs)
where |xs| == 0 || all { i in 1 .. |xs| | xs[i-1] + 1 == xs[i] }

property consecutive_b(int[] xs)
where |xs| == 0 || all { i in 0 .. |xs|-1 | xs[i] + 1 == xs[i+1] }

function f1(int[] xs) -> (int[] ys)
requires sorted_a(xs)
ensures sorted_a(ys):
    return xs

function f2(int[] xs) -> (int[] ys)
requires sorted_a(xs)
ensures sorted_b(ys):
    return xs

function f3(int[] xs) -> (int[] ys)
requires sorted_b(xs)
ensures sorted_a(ys):
    return xs

function f4(int[] xs) -> (int[] ys)
requires sorted_b(xs)
ensures sorted_b(ys):
    return xs

function g1(int[] xs) -> (int[] ys)
requires consecutive_a(xs)
ensures consecutive_a(ys):
    return xs

function g2(int[] xs) -> (int[] ys)
requires consecutive_a(xs)
ensures consecutive_b(ys):
    return xs

function g3(int[] xs) -> (int[] ys)
requires consecutive_b(xs)
ensures consecutive_a(ys):
    return xs

function g4(int[] xs) -> (int[] ys)
requires consecutive_b(xs)
ensures consecutive_b(ys):
    return xs

function h1(int[] xs) -> (int[] ys)
requires consecutive_a(xs)
ensures sorted_a(ys):
    return xs

function h2(int[] xs) -> (int[] ys)
requires consecutive_b(xs)
ensures sorted_a(ys):
    return xs

function h3(int[] xs) -> (int[] ys)
requires consecutive_a(xs)
ensures sorted_b(ys):
    return xs

function h4(int[] xs) -> (int[] ys)
requires consecutive_b(xs)
ensures sorted_b(ys):
    return xs

method test(int[] xs)
// All indices must match contents
requires all { i in 0..|xs| | xs[i] == i }:
    int[] ys
    //
    assert sorted_a(f1(xs))
    assert sorted_a(f2(xs))
    assert sorted_a(f3(xs))
    assert sorted_a(f4(xs))
    //
    assert sorted_a(g1(xs))
    assert sorted_a(g2(xs))
    assert sorted_a(g3(xs))
    assert sorted_a(g4(xs))
    //
    assert sorted_a(h1(xs))
    assert sorted_a(h2(xs))
    assert sorted_a(h3(xs))
    assert sorted_a(h4(xs))

public export method test():
    test([])
    test([0])
    test([0,1])
    test([0,1,2])
    test([0,1,2,3])
    test([0,1,2,3,4])
    test([0,1,2,3,4,5])
    test([0,1,2,3,4,5,6])
