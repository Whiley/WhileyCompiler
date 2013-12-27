import println from whiley.lang.System

define digits as [
    '0','1','2','3','4','5','6','7','8','9'
]

define alphabet as [
    'a','b','c','d','e','f'
]

string iof(int i):
    return "" + alphabet[i%6] + digits[i%10]

void ::main(System.Console sys):
    for i in 0..100:
        sys.out.println(Any.toString(iof(i)))
