define nat as int where $ >= 0

nat sum([nat] ls):
    int i=0
    int sum = 0
    while i < |ls| where i >= 0 && sum >= 0:
        sum = sum + ls[i]
        i = i + 1
    return sum

void System::main([string] args):
    print str(sum([]))
    print str(sum([1,2,3]))
    print str(sum([12387,98123,12398,12309,0]))
