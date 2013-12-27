import println from whiley.lang.System

void ::main(System.Console sys):
	x = "abcdefghjkl"
	y = x[..2]
	sys.out.println(y)
	y = x[1..3]
	sys.out.println(y)
	y = x[2..]
	sys.out.println(y)
