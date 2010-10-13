define nat as int where $ >= 0

nat sum([nat] ls):
    i=0
    sum = 0
    while i < |ls| where i >= 0 && sum >= 0:
        sum = sum + ls[i]
        i = i + 1
    return sum

void System::main([string] args):
    out->println(str(sum([])))
    out->println(str(sum([1,2,3])))
    out->println(str(sum([12387,98123,12398,12309,0])))
