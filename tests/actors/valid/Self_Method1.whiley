// Tests that calling an internal method correctly yields.
define Actor as process { int x }

void System::main([string] args):
    act = spawn { x: 1 }
    act!run(out)

void Actor::run(SystemOutWriter out):
    this.self(out)
    out.println(str(this.x))

void Actor::self(SystemOutWriter out):
    out.println(str(this.x))
    this.x = 2
