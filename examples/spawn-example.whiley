define process (int x, int y) as PointProc

void PointProc::update(int z):
    this->y = z

void System::main([string] args):
    PointProc pp = spawn (x:1,y:2)
    print str(*pp)
    pp->update(3)
    print str(*pp)