import * from whiley.lang.*

define nat as int

nat sum([nat] ls):
    i=0
    sum = 0
    while i < |ls|:
        sum = sum + ls[i]
        i = i + 1
    return sum

void ::main(System sys,[string] args):
    sys.out.println(str(sum([])))
    sys.out.println(str(sum([1,2,3])))
    sys.out.println(str(sum([12387,98123,12398,12309,0])))
