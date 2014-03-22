# -=[ ictk ]=- Internet Chess Toolkit 

Internet Chess ToolKit is a java based set of libraries and widgets useful 
for performing common tasks such as reading PGN, FEN, and generating 
legal moves. The net libraries convert ICS (Internet Chess Server) output 
into java POJOs for easy use by a client.

## SPECIFICATIONS & DESIGN

   If you're looking for a super fast Java implementation of a chess model 
to use as a basis for a chess engine you need to look elsewhere.  The 
design goal of this library has never been speed.  If it were it would 
create a LOT fewer objects.  Instead, the goal of ictk is to render chess
in a natural object oriented way that is both easy to use and well documented.  

   This library started out in 1997 as a proof of Object Oriented Design 
concepts.  The chess pieces act as agents and generate their own moves after
analysis of the board state.  A move object is an implementation of a 
Command Pattern.  And so on.  

   The Internet Chess Server (ICS/FICS) support converts the server's telent
text into POJOs, implementing a MVC based EventListener model for client code
to easily process.  As Java is overly verbose, and the messages are relatively
simplistic, the parsing is done in XML and converted to Java POJOs via XSTL.
The approach may seem overly complex to you, but saves a lot of time.

   Finally, this is not a graphics library, it is a game modeling library.
It provides for reading and writing standard game notations as well.
   
## FEATURES
* Object oriented Chess game model (MVC based)
* Legal move generation
* Game history with variation support (alternative move suggestions)
* Move comment support in text and Numeric Annotation Glyphs (NAG)
* Standard Algebraic Notation (SAN) read/write support (internationalized
  for presentation in 16 languages)
* Portable Game Notation (PGN) read/write support (including move variations,
  and FEN)
* Forsyth-Edwards Notation (FEN) read/write support
* True MVC structure with board displays driven by game model events
* Command-line Board display
* Well documented. Sample code also provided.
* FICS support [(freechess.org)](http://www.freechess.org)

## REQUIREMENTS

* [JDK 1.7](http://java.oracle.com) or better - Code now uses typed data
  structures and leverages the <> operator which is a new 1.7 feature.  If
  you would like to use JDK 1.4 - 1.6  the ictk v0.2 tag should work.
  Otherwise get the jar from [sf.net](http://ictk.sourceforge.net). 
     
* [Ant 1.5](http://ant.apache.org) or better - Only required to generate the    
  source through XSLT and compile the code, of course.

* [JUnit 3.8.0](http://junit.org) or better - Only necessary if you wish 
   to run the regression testing suite.

## LICENSE

   ictk is distributed under the GNU Public License version 2.  A copy of
this license is available in the file named COPYING, which should have
been included with this distribution.  

   If you wish to use this library for propriety purposes you must contact
the author.

## INSTALLATION

### From Source
The following commands will retrieve the latest source, generate the source code through XSLT, compile, create the Java-Doc and make the full jar file.  Just add the jar file to your `CLASSPATH`.

    $ git clone https://github.com/jvarsoke/ictk
    $ cd ictk
    $ ant build

### From Binary (Jar)
Download the release file jar and add it to your classpath.

##DEBUGGING

By default the library come with debugging info compiled in. (Unfortunately,
the library pre-dates [Log4j] and similar, so uses its own homegrown version).
From the command line you can turn the debugging info on by using the 
`java -D options` to set a system property.  For example:

    $ java -DDebug.History=1 Foo.java

If you really need a little more speed and you don't ever want to 
see debugging info (if you really think ictk is that stable) you can
change the `itck.util.Log.debug` variable to false.  This will make 
debugging impossible and eliminate all the conditional checks for
debugging.  Then you need to compile the optimized source (getting rid
of java internal debugging info).  To do this:

    $ ant clean
    $ ant optimize

Or if you wish to create an optimized jar file:

    $ ant optimized-jar

##TESTING
   If you wish to run the test suite, perhaps to see if it's your code that's
choking on a PGN file or ictk code, you can do so through ant and JUnit:

    $ ant test
