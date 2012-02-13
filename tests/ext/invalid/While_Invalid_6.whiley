

int sumOver([int] ls):
    i = 0
    sum = 0
    // now do the reverse!
    while i < |ls| where i >= 0 && sum >= 0:
        sum = sum + ls[i]
        i = i + 1
    return sum

void ::main(System.Console sys):
    rs = sumOver([-2,-3,1,2,-23,3,2345,4,5])
    debug Any.toString(rs)
