# Generated automatically from Makefile.in by configure.
# $Id$

# This software is subject to the terms of the IBM Jikes Compiler
# License Agreement available at the following URL:
# http://www.ibm.com/research/jikes.
# Copyright (C) 1996, 1998, International Business Machines Corporation
# and others.  All Rights Reserved.
# You must accept the terms of that agreement to use this software.


CC=c++
CCREQUIREDFLAGS=-funsigned-char -c -ansi -DTYPE_bool -DUNIX -DSTAT_POSIX -I/home/gjb/jikes/src
CCDEBUGFLAGS=-DTEST -g
CCOPTFLAGS=-O2
CCFLAGS=$(CCREQUIREDFLAGS) $(CCDEBUGFLAGS)
ERASE=rm -f
EXECUTABLE=jikes
LD=c++
LDFLAGS=-L. -o $(EXECUTABLE)
LIBS=
SYSOBJECTS=



BUILD_DIR=/home/gjb/jikes/src

srcdir		= /home/gjb/jikes/src
prefix		= /usr/local
exec_prefix	= ${prefix}


#
# You should not need to edit below here
#


all:  $(EXECUTABLE)

clean:
	$(ERASE) $(EXECUTABLE)
	$(ERASE) $(OBJECTS)
	$(ERASE) core

distclean: clean
	$(ERASE) config.cache config.log config.status
	$(ERASE) Makefile TAGS
	$(ERASE) *~

install: all
	mkdir -p $(prefix)/bin
	strip $(EXECUTABLE)
	cp $(EXECUTABLE) $(prefix)/bin
	mkdir -p $(prefix)/man/man1
	cp ../jikes.1 $(prefix)/man/man1

OBJECTS=jikes.o scanner.o code.o zip.o unzip.o \
        ast.o diagnose.o parser.o lpginput.o javaact.o \
        error.o set.o symbol.o lookup.o long.o depend.o \
        stream.o op.o control.o modifier.o system.o \
        bytecode.o body.o decl.o definite.o dump.o expr.o \
        getclass.o init.o double.o config.o option.o \
        incrmnt.o case.o tab.o segment.o unparse.o

$(EXECUTABLE):	$(OBJECTS)
	$(LD) $(LDFLAGS)  $(OBJECTS) $(SYSOBJECTS) $(LIBS)


javasym.h: $(srcdir)/java.g
	jikespg $(srcdir)/java.g
	$(ERASE) java.l

javadef.h: $(srcdir)/java.g
	jikespg $(srcdir)/java.g
	$(ERASE) java.l

javadcl.h: $(srcdir)/java.g
	jikespg $(srcdir)/java.g
	$(ERASE) java.l

javaprs.h: $(srcdir)/java.g
	jikespg $(srcdir)/java.g
	$(ERASE) java.l


ast.o:  $(srcdir)/ast.cpp $(srcdir)/config.h \
          $(srcdir)/code.h $(srcdir)/case.h $(srcdir)/ast.h \
          $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
          $(srcdir)/tuple.h $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
          $(srcdir)/double.h $(srcdir)/symbol.h $(srcdir)/option.h \
          $(srcdir)/depend.h $(srcdir)/access.h 
	$(CC) $(CCFLAGS) $(srcdir)/ast.cpp 

unparse.o:  $(srcdir)/unparse.cpp $(srcdir)/config.h \
          $(srcdir)/code.h $(srcdir)/case.h $(srcdir)/ast.h \
          $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
          $(srcdir)/tuple.h $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
          $(srcdir)/double.h $(srcdir)/symbol.h $(srcdir)/option.h \
          $(srcdir)/depend.h $(srcdir)/access.h 
	$(CC) $(CCFLAGS) $(srcdir)/unparse.cpp 

body.o: $(srcdir)/body.cpp $(srcdir)/config.h $(srcdir)/semantic.h \
          $(srcdir)/ast.h $(srcdir)/stream.h $(srcdir)/javadef.h \
          $(srcdir)/javasym.h $(srcdir)/tuple.h \
          $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
          $(srcdir)/double.h $(srcdir)/code.h \
	  $(srcdir)/case.h $(srcdir)/symbol.h $(srcdir)/option.h \
          $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/diagnose.h \
          $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
          $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/body.cpp 

bytecode.o: $(srcdir)/bytecode.cpp $(srcdir)/config.h $(srcdir)/ast.h \
              $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
              $(srcdir)/tuple.h $(srcdir)/tab.h \
              $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
              $(srcdir)/code.h $(srcdir)/case.h \
              $(srcdir)/symbol.h $(srcdir)/option.h $(srcdir)/depend.h \
              $(srcdir)/access.h $(srcdir)/bytecode.h $(srcdir)/class.h \
              $(srcdir)/semantic.h $(srcdir)/diagnose.h $(srcdir)/parser.h \
              $(srcdir)/lpginput.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
              $(srcdir)/error.h $(srcdir)/control.h $(srcdir)/set.h \
              $(srcdir)/op.h $(srcdir)/segment.h  \
              $(srcdir)/op.h $(srcdir)/table.h 
	$(CC) $(CCFLAGS) $(srcdir)/bytecode.cpp 

config.o:  $(srcdir)/config.cpp $(srcdir)/config.h 
	$(CC) $(CCFLAGS) $(srcdir)/config.cpp 

code.o:  $(srcdir)/code.cpp $(srcdir)/code.h $(srcdir)/config.h 
	$(CC) $(CCFLAGS) $(srcdir)/code.cpp 

control.o:  $(srcdir)/control.cpp $(srcdir)/config.h $(srcdir)/control.h \
              $(srcdir)/option.h $(srcdir)/code.h \
              $(srcdir)/tuple.h $(srcdir)/symbol.h $(srcdir)/stream.h \
              $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tab.h \
              $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
              $(srcdir)/case.h $(srcdir)/depend.h \
              $(srcdir)/access.h $(srcdir)/set.h $(srcdir)/scanner.h \
	      $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
              $(srcdir)/javaact.h $(srcdir)/semantic.h $(srcdir)/ast.h \
	      $(srcdir)/diagnose.h $(srcdir)/error.h $(srcdir)/bytecode.h \
              $(srcdir)/class.h $(srcdir)/segment.h \
              $(srcdir)/class.h $(srcdir)/op.h 
	$(CC) $(CCFLAGS) $(srcdir)/control.cpp 

case.o:  $(srcdir)/case.cpp $(srcdir)/config.h $(srcdir)/case.h 
	$(CC) $(CCFLAGS) $(srcdir)/case.cpp 

diagnose.o:  $(srcdir)/diagnose.cpp $(srcdir)/config.h $(srcdir)/diagnose.h \
               $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/stream.h \
	       $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tuple.h \
               $(srcdir)/tab.h $(srcdir)/lookup.h \
               $(srcdir)/long.h $(srcdir)/double.h \
               $(srcdir)/code.h $(srcdir)/case.h $(srcdir)/javaprs.h \
               $(srcdir)/javaact.h $(srcdir)/control.h $(srcdir)/option.h \
	       $(srcdir)/symbol.h $(srcdir)/depend.h $(srcdir)/access.h \
               $(srcdir)/set.h $(srcdir)/semantic.h $(srcdir)/ast.h \
               $(srcdir)/error.h $(srcdir)/spell.h
	$(CC) $(CCFLAGS) $(srcdir)/diagnose.cpp 

decl.o:  $(srcdir)/decl.cpp $(srcdir)/config.h $(srcdir)/semantic.h \
           $(srcdir)/ast.h $(srcdir)/stream.h $(srcdir)/javadef.h \
           $(srcdir)/javasym.h $(srcdir)/tuple.h \
           $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
           $(srcdir)/double.h $(srcdir)/code.h \
	   $(srcdir)/case.h $(srcdir)/symbol.h $(srcdir)/option.h \
           $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/diagnose.h \
           $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
           $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h \
           $(srcdir)/set.h $(srcdir)/table.h 
	$(CC) $(CCFLAGS) $(srcdir)/decl.cpp 

definite.o:  $(srcdir)/definite.cpp $(srcdir)/config.h $(srcdir)/semantic.h \
               $(srcdir)/ast.h $(srcdir)/stream.h $(srcdir)/javadef.h \
	       $(srcdir)/javasym.h $(srcdir)/tuple.h \
               $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
               $(srcdir)/double.h $(srcdir)/code.h \
               $(srcdir)/case.h $(srcdir)/symbol.h $(srcdir)/option.h \
               $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/diagnose.h \
	       $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
               $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h \
               $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/definite.cpp 

depend.o:  $(srcdir)/depend.cpp $(srcdir)/config.h $(srcdir)/ast.h \
             $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
             $(srcdir)/tuple.h $(srcdir)/tab.h \
             $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
             $(srcdir)/code.h $(srcdir)/case.h \
	     $(srcdir)/symbol.h $(srcdir)/option.h $(srcdir)/depend.h \
             $(srcdir)/access.h $(srcdir)/semantic.h $(srcdir)/diagnose.h \
	     $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
             $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h \
             $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/depend.cpp 

double.o:  $(srcdir)/double.cpp $(srcdir)/config.h $(srcdir)/double.h \
             $(srcdir)/long.h 
	$(CC) $(CCFLAGS) $(srcdir)/double.cpp 

dump.o:  $(srcdir)/dump.cpp $(srcdir)/config.h $(srcdir)/javasym.h \
           $(srcdir)/control.h $(srcdir)/option.h \
           $(srcdir)/code.h $(srcdir)/tuple.h $(srcdir)/symbol.h \
           $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/tab.h \
           $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
           $(srcdir)/case.h $(srcdir)/depend.h \
           $(srcdir)/access.h $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/dump.cpp 

error.o:  $(srcdir)/error.cpp $(srcdir)/config.h \
            $(srcdir)/code.h $(srcdir)/case.h \
            $(srcdir)/error.h $(srcdir)/stream.h $(srcdir)/javadef.h \
            $(srcdir)/javasym.h $(srcdir)/tuple.h $(srcdir)/tab.h \
            $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
            $(srcdir)/control.h $(srcdir)/option.h $(srcdir)/symbol.h \
            $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/set.h \
	    $(srcdir)/semantic.h $(srcdir)/ast.h $(srcdir)/diagnose.h \
            $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
	    $(srcdir)/javaact.h 
	$(CC) $(CCFLAGS) $(srcdir)/error.cpp 

expr.o:  $(srcdir)/expr.cpp $(srcdir)/double.h $(srcdir)/config.h \
           $(srcdir)/parser.h $(srcdir)/lpginput.h \
           $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
           $(srcdir)/tuple.h $(srcdir)/tab.h $(srcdir)/lookup.h \
           $(srcdir)/long.h $(srcdir)/code.h \
           $(srcdir)/case.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
           $(srcdir)/semantic.h $(srcdir)/ast.h $(srcdir)/symbol.h \
	   $(srcdir)/option.h $(srcdir)/depend.h $(srcdir)/access.h \
           $(srcdir)/diagnose.h $(srcdir)/error.h $(srcdir)/control.h \
           $(srcdir)/set.h $(srcdir)/table.h $(srcdir)/spell.h
	$(CC) $(CCFLAGS) $(srcdir)/expr.cpp 

getclass.o:  $(srcdir)/getclass.cpp $(srcdir)/config.h $(srcdir)/control.h \
               $(srcdir)/option.h $(srcdir)/code.h \
               $(srcdir)/tuple.h $(srcdir)/symbol.h $(srcdir)/stream.h \
               $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tab.h \
               $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
               $(srcdir)/case.h $(srcdir)/depend.h \
               $(srcdir)/access.h $(srcdir)/set.h $(srcdir)/semantic.h \
	       $(srcdir)/ast.h $(srcdir)/diagnose.h $(srcdir)/parser.h \
               $(srcdir)/lpginput.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
               $(srcdir)/error.h $(srcdir)/getclass.h $(srcdir)/zip.h \
               $(srcdir)/unzip.h 
	$(CC) $(CCFLAGS) $(srcdir)/getclass.cpp 

incrmnt.o:  $(srcdir)/incrmnt.cpp $(srcdir)/config.h $(srcdir)/control.h \
              $(srcdir)/option.h $(srcdir)/code.h \
              $(srcdir)/tuple.h $(srcdir)/symbol.h $(srcdir)/stream.h \
              $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tab.h \
              $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
              $(srcdir)/case.h $(srcdir)/depend.h \
              $(srcdir)/access.h $(srcdir)/set.h $(srcdir)/scanner.h \
	      $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
              $(srcdir)/javaact.h $(srcdir)/semantic.h $(srcdir)/ast.h \
	      $(srcdir)/diagnose.h $(srcdir)/error.h 
	$(CC) $(CCFLAGS) $(srcdir)/incrmnt.cpp 

init.o:  $(srcdir)/init.cpp $(srcdir)/config.h $(srcdir)/semantic.h \
           $(srcdir)/ast.h $(srcdir)/stream.h $(srcdir)/javadef.h \
           $(srcdir)/javasym.h $(srcdir)/tuple.h \
           $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
           $(srcdir)/double.h $(srcdir)/code.h \
	   $(srcdir)/case.h $(srcdir)/symbol.h $(srcdir)/option.h \
           $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/diagnose.h \
           $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
           $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/init.cpp

javaact.o: $(srcdir)/javaact.cpp $(srcdir)/config.h $(srcdir)/parser.h \
             $(srcdir)/lpginput.h $(srcdir)/stream.h $(srcdir)/javadef.h \
	     $(srcdir)/javasym.h $(srcdir)/tuple.h \
             $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
             $(srcdir)/double.h $(srcdir)/code.h \
             $(srcdir)/case.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
             $(srcdir)/ast.h $(srcdir)/symbol.h $(srcdir)/option.h \
	     $(srcdir)/depend.h $(srcdir)/access.h 
	$(CC) $(CCFLAGS) $(srcdir)/javaact.cpp

jikes.o:  $(srcdir)/jikes.cpp $(srcdir)/config.h $(srcdir)/control.h \
            $(srcdir)/option.h $(srcdir)/code.h \
            $(srcdir)/tuple.h $(srcdir)/symbol.h $(srcdir)/stream.h \
            $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tab.h \
            $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
            $(srcdir)/case.h $(srcdir)/depend.h \
            $(srcdir)/access.h $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/jikes.cpp

long.o:  $(srcdir)/long.cpp $(srcdir)/config.h $(srcdir)/long.h \
           $(srcdir)/double.h 
	$(CC) $(CCFLAGS) $(srcdir)/long.cpp 

lookup.o:  $(srcdir)/lookup.cpp $(srcdir)/config.h $(srcdir)/lookup.h \
             $(srcdir)/tuple.h $(srcdir)/long.h \
             $(srcdir)/double.h $(srcdir)/symbol.h $(srcdir)/code.h \
             $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
             $(srcdir)/tab.h $(srcdir)/case.h \
             $(srcdir)/option.h $(srcdir)/depend.h $(srcdir)/access.h \
             $(srcdir)/ast.h 
	$(CC) $(CCFLAGS) $(srcdir)/lookup.cpp

lpginput.o:  $(srcdir)/lpginput.cpp $(srcdir)/config.h $(srcdir)/lpginput.h \
               $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
	       $(srcdir)/tuple.h $(srcdir)/tab.h \
               $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
               $(srcdir)/code.h $(srcdir)/case.h \
               $(srcdir)/javaprs.h $(srcdir)/javadcl.h 
	$(CC) $(CCFLAGS) $(srcdir)/lpginput.cpp 

modifier.o:  $(srcdir)/modifier.cpp $(srcdir)/config.h $(srcdir)/semantic.h \
               $(srcdir)/ast.h $(srcdir)/stream.h $(srcdir)/javadef.h \
	       $(srcdir)/javasym.h $(srcdir)/tuple.h \
               $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
               $(srcdir)/double.h $(srcdir)/code.h \
               $(srcdir)/case.h $(srcdir)/symbol.h $(srcdir)/option.h \
               $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/diagnose.h \
	       $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
               $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h \
               $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/modifier.cpp 

op.o:  $(srcdir)/op.cpp $(srcdir)/config.h $(srcdir)/op.h $(srcdir)/tuple.h \
         $(srcdir)/class.h $(srcdir)/semantic.h $(srcdir)/ast.h \
	 $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
         $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
	 $(srcdir)/code.h $(srcdir)/case.h $(srcdir)/symbol.h \
         $(srcdir)/option.h $(srcdir)/depend.h $(srcdir)/access.h \
         $(srcdir)/diagnose.h $(srcdir)/parser.h $(srcdir)/lpginput.h \
         $(srcdir)/javaprs.h $(srcdir)/javaact.h $(srcdir)/error.h \
	 $(srcdir)/control.h $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/op.cpp 

option.o:  $(srcdir)/option.cpp $(srcdir)/option.h $(srcdir)/config.h \
             $(srcdir)/code.h $(srcdir)/tuple.h \
             $(srcdir)/javasym.h $(srcdir)/error.h $(srcdir)/stream.h \
             $(srcdir)/javadef.h $(srcdir)/tab.h $(srcdir)/lookup.h \
             $(srcdir)/long.h $(srcdir)/double.h \
             $(srcdir)/case.h 
	$(CC) $(CCFLAGS) $(srcdir)/option.cpp 

parser.o:  $(srcdir)/parser.cpp $(srcdir)/config.h $(srcdir)/parser.h \
             $(srcdir)/lpginput.h $(srcdir)/stream.h $(srcdir)/javadef.h \
	     $(srcdir)/javasym.h $(srcdir)/tuple.h \
             $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
             $(srcdir)/double.h $(srcdir)/code.h \
             $(srcdir)/case.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
             $(srcdir)/ast.h $(srcdir)/symbol.h $(srcdir)/option.h \
	     $(srcdir)/depend.h $(srcdir)/access.h 
	$(CC) $(CCFLAGS) $(srcdir)/parser.cpp

scanner.o: $(srcdir)/scanner.cpp $(srcdir)/config.h $(srcdir)/scanner.h \
             $(srcdir)/code.h $(srcdir)/javadef.h \
             $(srcdir)/javasym.h $(srcdir)/stream.h $(srcdir)/tuple.h \
             $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
             $(srcdir)/double.h $(srcdir)/case.h \
             $(srcdir)/control.h $(srcdir)/option.h $(srcdir)/symbol.h \
             $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/set.h $(srcdir)/error.h 
	$(CC) $(CCFLAGS) $(srcdir)/scanner.cpp 

set.o:  $(srcdir)/set.cpp $(srcdir)/set.h $(srcdir)/config.h \
          $(srcdir)/symbol.h $(srcdir)/code.h \
          $(srcdir)/stream.h $(srcdir)/javadef.h $(srcdir)/javasym.h \
          $(srcdir)/tuple.h $(srcdir)/tab.h $(srcdir)/lookup.h \
          $(srcdir)/long.h $(srcdir)/double.h \
          $(srcdir)/case.h $(srcdir)/option.h $(srcdir)/depend.h \
          $(srcdir)/access.h 
	$(CC) $(CCFLAGS) $(srcdir)/set.cpp 

stream.o:  $(srcdir)/stream.cpp $(srcdir)/config.h $(srcdir)/stream.h \
             $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tuple.h \
             $(srcdir)/tab.h $(srcdir)/lookup.h \
             $(srcdir)/long.h $(srcdir)/double.h \
             $(srcdir)/code.h $(srcdir)/case.h $(srcdir)/zip.h \
	     $(srcdir)/unzip.h $(srcdir)/symbol.h $(srcdir)/option.h \
             $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/control.h \
             $(srcdir)/set.h $(srcdir)/semantic.h $(srcdir)/ast.h \
             $(srcdir)/diagnose.h $(srcdir)/parser.h $(srcdir)/lpginput.h \
             $(srcdir)/javaprs.h $(srcdir)/javaact.h $(srcdir)/error.h 
	$(CC) $(CCFLAGS) $(srcdir)/stream.cpp 

symbol.o:  $(srcdir)/symbol.cpp $(srcdir)/config.h $(srcdir)/stream.h \
             $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tuple.h \
             $(srcdir)/tab.h $(srcdir)/lookup.h \
             $(srcdir)/long.h $(srcdir)/double.h \
             $(srcdir)/code.h $(srcdir)/case.h $(srcdir)/control.h \
	     $(srcdir)/option.h $(srcdir)/symbol.h $(srcdir)/depend.h \
             $(srcdir)/access.h $(srcdir)/set.h $(srcdir)/ast.h \
             $(srcdir)/semantic.h $(srcdir)/diagnose.h $(srcdir)/parser.h \
             $(srcdir)/lpginput.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
             $(srcdir)/error.h $(srcdir)/table.h $(srcdir)/zip.h $(srcdir)/unzip.h 
	$(CC) $(CCFLAGS) $(srcdir)/symbol.cpp

system.o:  $(srcdir)/system.cpp $(srcdir)/config.h $(srcdir)/control.h \
             $(srcdir)/option.h $(srcdir)/code.h \
             $(srcdir)/tuple.h $(srcdir)/symbol.h $(srcdir)/stream.h \
             $(srcdir)/javadef.h $(srcdir)/javasym.h $(srcdir)/tab.h \
             $(srcdir)/lookup.h $(srcdir)/long.h $(srcdir)/double.h \
             $(srcdir)/case.h $(srcdir)/depend.h \
             $(srcdir)/access.h $(srcdir)/set.h $(srcdir)/semantic.h \
	     $(srcdir)/ast.h $(srcdir)/diagnose.h $(srcdir)/parser.h \
             $(srcdir)/lpginput.h $(srcdir)/javaprs.h $(srcdir)/javaact.h \
             $(srcdir)/error.h $(srcdir)/zip.h $(srcdir)/unzip.h 
	$(CC) $(CCFLAGS) $(srcdir)/system.cpp 

tab.o: $(srcdir)/tab.h $(srcdir)/tab.cpp $(srcdir)/config.h
	$(CC) $(CCFLAGS) $(srcdir)/tab.cpp

unzip.o:  $(srcdir)/unzip.cpp $(srcdir)/config.h $(srcdir)/unzip.h 
	$(CC) $(CCFLAGS) $(srcdir)/unzip.cpp 

wom.o:  $(srcdir)/wom.cpp $(srcdir)/config.h $(srcdir)/semantic.h \
          $(srcdir)/ast.h $(srcdir)/stream.h $(srcdir)/javadef.h \
          $(srcdir)/javasym.h $(srcdir)/tuple.h \
          $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
          $(srcdir)/double.h $(srcdir)/code.h \
	  $(srcdir)/case.h $(srcdir)/symbol.h $(srcdir)/option.h \
          $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/diagnose.h \
          $(srcdir)/parser.h $(srcdir)/lpginput.h $(srcdir)/javaprs.h \
          $(srcdir)/javaact.h $(srcdir)/error.h $(srcdir)/control.h $(srcdir)/set.h 
	$(CC) $(CCFLAGS) $(srcdir)/wom.cpp

zip.o:  $(srcdir)/zip.cpp $(srcdir)/config.h $(srcdir)/control.h \
          $(srcdir)/option.h $(srcdir)/code.h $(srcdir)/tuple.h \
	  $(srcdir)/symbol.h $(srcdir)/stream.h $(srcdir)/javadef.h \
          $(srcdir)/javasym.h $(srcdir)/tab.h $(srcdir)/lookup.h $(srcdir)/long.h \
	  $(srcdir)/double.h $(srcdir)/case.h \
          $(srcdir)/depend.h $(srcdir)/access.h $(srcdir)/set.h \
          $(srcdir)/zip.h $(srcdir)/unzip.h 
	$(CC) $(CCFLAGS) $(srcdir)/zip.cpp 

segment.o:  $(srcdir)/segment.cpp $(srcdir)/segment.h $(srcdir)/tuple.h $(srcdir)/config.h 
	$(CC) $(CCFLAGS) $(srcdir)/segment.cpp


tags:
	etags *.h *.cpp

TAGS:  tags
