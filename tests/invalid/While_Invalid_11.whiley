
function sumOver([int] ls) -> int:
    int i = 0
    int sum = 0
    //
    while i < |ls| where (i >= 0) && (sum >= 0):
        sum = sum + ls[i]
        i = i + 1
    //
    return sum
