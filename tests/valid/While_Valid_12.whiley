import println from whiley.lang.System

int|null indexOf([int] xs, int x) ensures $ is null || xs[$] == x:
	i = 0
	while i < |xs| where i >= 0:
		if xs[i] == x:
			return i
		i = i + 1
	return null

void ::main(System.Console console):		
	console.out.println(indexOf([1,2,3],1))
	console.out.println(indexOf([1,2,3],0))
	




