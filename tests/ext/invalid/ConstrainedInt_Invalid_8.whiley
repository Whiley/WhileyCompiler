import * from whiley.lang.*

define frf1nat as int where $ >= 0

void f(frf1nat y):
    debug "F(NAT)"

void f(int x):
    debug "F(INT)"

void ::main(System sys,[string] args):
    f(-1)
    f(1)
