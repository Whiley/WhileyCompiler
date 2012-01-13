import * from whiley.lang.*

bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '-'

void ::main(System.Console sys):
    debug "" + isWhiteSpace('-')
