import whiley.lang.*:*

int g(int x) ensures $ > 0 && $ < 125:
    if(x <= 0 || x >= 125):
        return 1
    else:
        return x

{int8} f(int x):
    return {g(x)}

void ::main(System sys,[string] args):
    bytes = f(0)
    sys.out.println(str(bytes))

