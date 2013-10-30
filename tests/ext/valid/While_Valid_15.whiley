import println from whiley.lang.System

int f(int n):
	x = 0
	y = 0
	while x < n where y == 2*x:
		x = x + 1
		y = y + 2
	return x + y

void ::main(System.Console console):
	console.out.println(f(10))
	
	




