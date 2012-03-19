import * from whiley.lang.*

define Actor as ref { int x }

void ::main(Console sys):
    act = new { x: 1 }
    act!run(sys.out)

// Tests that calling an internal method correctly yields.
void Actor::run(SystemOutWriter out):
    this.self(out)
    out.println(this->x)

void Actor::self(SystemOutWriter out):
    out.println(this->x)
    this->x = 2
