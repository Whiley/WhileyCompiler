

type nat is (int x) where x >= 0

function sum(nat[] ls) -> nat:
    int i = 0
    int sum = 0
    while i < |ls| where i >= 0 && sum >= 0:
        sum = sum + ls[i]
        i = i + 1
    return sum

public export method test() :
    assume sum([0;0]) == 0
    assume sum([1, 2, 3]) == 6
    assume sum([12387, 98123, 12398, 12309, 0]) == 135217

