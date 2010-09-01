define nat as int where $ >= 0
void System::main([string] args):
    [nat] xs = [1,2,3]
    int r = 0
    for x in xs where r >= 0:
        r = r + x    
    print str(r)
