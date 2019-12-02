public function sum(int[][] items) -> int:
    int r = 0
    for i in 0..|items|:
        int[] ith = items[i]
        for j in 0..|ith|:
            r = r + ith[j]
    // Done
    return r
    
public export method test():
    assume sum([[1]]) == 1
    assume sum([[1,2]]) == 3
    assume sum([[1,2],[3]]) == 6
    assume sum([[1,2],[3],[4,5,6]]) == 21