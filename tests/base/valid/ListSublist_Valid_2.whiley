import whiley.lang.*:*

void ::main(System sys,[string] args):
    list = [1,2,3]
    sublist = list[..2]
    sys.out.println(str(list))
    sys.out.println(str(sublist))
