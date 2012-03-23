import * from whiley.lang.*

define Actor as ref { int state }

// Tests that variables are maintained after synchronous message sends.
void ::main(Console sys):
    actor = new { state: 6 }
    i = actor.state()
    actor->state = 5
    sys.out!println(actor.state())
    sys.out!println(i)

int Actor::state():
    return this->state
