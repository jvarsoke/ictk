SimpleChessClient logs into FICS and parses screen-scraped telnet messages,
turning them into ICTK event POJOs then redisplays them to the terminal
as colorized text with a time-stamp.

Rendering the events on a graphical board is left as a exercise to the coder.

To start the fun: 

    $ java SimpleChessClient <username> <password>

to log in as a guest

    $ java SimpleChessClient g -

