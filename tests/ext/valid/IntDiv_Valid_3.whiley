import println from whiley.lang.System

(int,int) f(int x, int y) requires x == 2*y:
	x = x + 2
	y = y + 1
	assert 2*y == x
	return x,y

public void ::main(System.Console console):
	console.out.println(f(2,1))

	
