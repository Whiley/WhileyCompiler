type arr_t is (int[] xs)
where all { i in *xs .. |xs| | xs[i] >= 0 }