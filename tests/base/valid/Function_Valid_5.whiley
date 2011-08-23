import whiley.lang.*:*

define fr5nat as int

{fr5nat} g({fr5nat} xs):
    return { y | y in xs, y > 1 }

string f({fr5nat} x):
    return str(x)

void ::main(System sys,[string] args):
    ys = {1,2,3}
    sys.out.println(f(g(ys)))
