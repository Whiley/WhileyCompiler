define nat as int
void System::main([string] args):
    xs = [1,2,3]
    r = 0
    for x in xs:
        r = r + x    
    this.out.println(str(r))
