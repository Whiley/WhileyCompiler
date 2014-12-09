import * from whiley.lang.*

method main(System.Console sys) -> void:
    int crc = 4294967295
    crc = crc ^ 11010101b
    sys.out.println(crc)
    crc = crc ^ 11010101b
