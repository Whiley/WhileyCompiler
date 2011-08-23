import whiley.lang.*:*

define fr3nat as int where $ >= 0

string f(int x):
    return str(x)

void ::main(System sys,[string] args):
    y = 1
    sys.out.println(f(y))
