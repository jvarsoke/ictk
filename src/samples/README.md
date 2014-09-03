# -=[ ictk ]=- Getting Started

In order to give you a feel for how easy the library is to use, a few samples
are provided to get you started in development.

## Building the ictk jar file

First you need to build the ictk jar file.  The following commands assume you
start in the root of the install directory, usually named "ictk".  You must
have maven installed to continue (Ant can be used, but is deprecated).  To
create the jar file, execute the following command.

    $ mvn package

## Running the sample programs

This will create ./target/ictk-[version].jar.  The best thing to do is to add
this to your CLASSPATH.  However, the following commands assume you don't
want to do that.  If you have, you can ignore the "-cp [jar]" options in
the following statements.

Next change directory to the sample you'd like to try out:

    $ cd src/samples/java/simpleChessGameExample
    $ javac -cp ../../../../target/ictk-[version].jar SimpleChessGameExample.java 
    $ java -cp ../../../../target/ictk-[version].jar:. simpleChessGameExample

That's it.  Of course, if you added ictk-[version].jar to your CLASSPATH you
could just:

    $ javac SimpleChessGameExample.java
    $ java SimpleChessGameExample

## The Samples

### SimpleChessGameExample

This program demonstrates how to programmatically enter moves through the ICTK API.

### SimplePGNDemo

This program reads in a PGN from the command line, writes the PGN to STDOUT, writes 
the final board's FEN, and calculates some statistics about the mainline of the game.

### RepCheck

This program demonstrates the API's ability to detect a 3-fold repetition in the game,
which is grounds for declaring a draw.  If no 3xRep occurred, there is no output. (typical
unix philosophy)

### CLIPGNViewer

Given a pgn file as a commandline argument to this program, you can use a simple text-based
interface for walking through the PGN, following variation lines, etc.

### SimpleICSClient

This is a simple ICS Chess Server client.  It logs in and color codes
the text.  There is no Board associated with it yet, so playing a 
game is not practical.  But it should give the simplist idea of how
to write an interface with the ICTK library.

## Final Note

Good luck.  Have fun.  If you run into problems, just drop me a line.

