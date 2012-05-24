import * from whiley.lang.*

void ::main(System.Console sys):
    crc = 0xffffffff
    crc = crc ^ 11010101b
    sys.out.println(crc)
    crc = crc ^ 11010101b