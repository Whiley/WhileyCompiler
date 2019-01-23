// Test static variable initialiser order
Color color = RED

final int RED = 1
final int BLUE = 2
final int GREEN = 3

type Color is (int x) where (x == RED) || (x == BLUE) || (x == GREEN)

public export method test():
    assert color == RED
