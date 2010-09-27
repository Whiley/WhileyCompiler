define PointProc as process {int x, int y}

void PointProc::update(int z):
    this->y = z

void System::main([string] args):
    pp = spawn {x:1, y:2}
    print str(*pp)
    pp->update(3)
    print str(*pp)
