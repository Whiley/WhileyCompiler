define nat as int where $ >= 0
void System::main([string] args):
    xs = [1,2,3]
    r = 0
    for x in xs where r >= 0:
        r = r + x    
    out->println(str(r))
