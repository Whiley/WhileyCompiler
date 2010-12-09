define nat as int where $ >= 0
void System::main([string] args):
    xs = [1,2,3]
    r = |args|-1
    for x in xs where r >= 0:
        r = r + x    
    print str(r)
