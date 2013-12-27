import println from whiley.lang.System

define nat as int where $ >= 0

nat f(nat x, int y) requires y > 0:
	if true:
		z = x / y
	else:
		z = y / x
	return z

void ::main(System.Console sys):
     x = f(10,2)
     sys.out.println(Any.toString(x)  )
