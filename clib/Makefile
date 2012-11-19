#../lib/wycc_lib.o:	../lib/wycc_lib.h ./wycc_lib.c
#	gcc -ggdb -c wycc_lib.c
	#mv wycc_lib.o ../lib

OBJS =	wycc_lib.o wycc_main.o wycc_FOM.o

wycc_lib.a:	$(OBJS)
	ar -rc wycc_lib.a $(OBJS)
	cp wycc_lib.a ../lib

wycc_lib.o:	../lib/wycc_lib.h common.h ytype.h wycc_lib.c

wycc_main.o:	../lib/wycc_lib.h common.h FOM.h wycc_main.c

wycc_FOM.o:	../lib/wycc_lib.h common.h FOM.h ytype.h wycc_FOM.c