# Makefile made by metamake run in /am/rialto/home1/protin/wy/clib

CFLAGS := -ggdb $(CFLAGS)

OBJS :=  wycc_FOM.o wycc__abs.o wycc__file.o wycc__isDigit.o wycc__isLetter.o wycc__isLowerCase.o wycc__isUpperCase.o wycc__isWhiteSpace.o wycc__isqrt.o wycc__max.o wycc__min.o wycc__toUnsignedByte.o wycc__toUnsignedInt.o wycc_indirect_invoke.o wycc_lib.o wycc_main.o wycc_math_stubbed.o wycc_math_gmp.o

install :	wycc_lib.a wycc_math_gmp.o
	cp $^ ../lib

wycc_lib.a :	$(OBJS)
	ar -rc $@ $(OBJS)
	ar -d $@ wycc_math_gmp.o
#	ar -mb wycc_lib.o $@ wycc_math_gmp.o


wycc_FOM.o: wycc_FOM.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h

wycc__abs.o: wycc__abs.c ../include/wycc_lib.h common.h box.h

wycc__file.o: wycc__file.c ../include/wycc_lib.h common.h box.h

wycc__isDigit.o: wycc__isDigit.c ../include/wycc_lib.h common.h box.h

wycc__isLetter.o: wycc__isLetter.c ../include/wycc_lib.h common.h box.h

wycc__isLowerCase.o: wycc__isLowerCase.c ../include/wycc_lib.h common.h box.h

wycc__isUpperCase.o: wycc__isUpperCase.c ../include/wycc_lib.h common.h box.h

wycc__isWhiteSpace.o: wycc__isWhiteSpace.c ../include/wycc_lib.h common.h \
 box.h

wycc__isqrt.o: wycc__isqrt.c ../include/wycc_lib.h common.h box.h

wycc__max.o: wycc__max.c ../include/wycc_lib.h common.h box.h

wycc__min.o: wycc__min.c ../include/wycc_lib.h common.h box.h

wycc__toUnsignedByte.o: wycc__toUnsignedByte.c ../include/wycc_lib.h common.h \
 box.h

wycc__toUnsignedInt.o: wycc__toUnsignedInt.c ../include/wycc_lib.h common.h \
 box.h

wycc_indirect_invoke.o: wycc_indirect_invoke.c ../include/wycc_lib.h common.h \
 FOM.h box.h

wycc_lib.o: wycc_lib.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h math.h

wycc_math_stubbed.o: wycc_lib.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h \
 math.h

wycc_math_gmp.o: wycc_lib.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h \
 math.h

wycc_main.o: wycc_main.c ../include/wycc_lib.h common.h FOM.h

