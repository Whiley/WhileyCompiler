import println from whiley.lang.System

define GAME as [
	(0,0), // circle
	(1,1), // cross
	(0,1), // circle
	(2,2), // cross
	(0,2), // circle
	(2,2)  // should be impossible
]

public void ::main(System.Console console):
    for tup in GAME:
        console.out.println("TUPLE: " + tup)
