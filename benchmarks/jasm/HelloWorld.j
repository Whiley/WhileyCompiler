.class public HelloWorld
.super java/lang/Object

.method public <init>()V
   aload_0
   invokenonvirtual java/lang/Object/<init>()V
   return
.end method

.method public static main([Ljava/lang/String;)V
   .limit locals 2
   .limit stack 4

   getstatic java/lang/System/out Ljava/io/PrintStream;
   ldc "Hello World"
   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

   return
.end method
