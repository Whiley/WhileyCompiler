

define frf1nat as int where $ >= 0

void f(frf1nat y):
    debug "F(NAT)"

void f(int x):
    debug "F(INT)"

void ::main(System.Console sys):
    f(-1)
    f(1)
