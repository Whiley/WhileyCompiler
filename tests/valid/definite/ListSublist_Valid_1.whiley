define posintlist as [int] where no { x in $ | x < 0 }

int sum(posintlist ls) where $ >= 0:
    if(|ls| == 0):
        return 0
    else:
        [int] rest = ls[1:|ls|]
        return ls[0] + sum(rest)

void System::main([string] args):
    int c = sum([1,2,3,4,5,6,7])
    print str(c)
    
