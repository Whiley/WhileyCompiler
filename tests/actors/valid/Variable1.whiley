import * from whiley.lang.*

define Actor as ref { int state }

// Tests that an actor can correctly use the result of a synchronous message.
void ::main(Console sys):
    actor = new { state: 6 }
    i = actor.state()
    sys.out!println(i)
    sys.out.println(i)

int Actor::state():
    return this->state
