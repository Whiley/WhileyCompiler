int SIZE = 5

public export method test() :
    int[][] components = [[]; SIZE]
    int i = 0
    while i < |components| where i >= 0:
        components[i] = [0]
        i = i + 1
    assume components == [[0],[0],[0],[0],[0]]
