// Test static variable initialiser order
final int RED = 1

Color color = RED + BLUE

final int BLUE = 2
final int GREEN = 3

type Color is (int x) where (x == RED) || (x == BLUE) || (x == GREEN)

public export method test():
    assert color == GREEN
