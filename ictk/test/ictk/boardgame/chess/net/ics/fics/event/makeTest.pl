#!/usr/bin/perl -w
#  ICTK - Internet Chess ToolKit
#  More information is available at http://ictk.sourceforge.net
#  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
#  All rights reserved.
#
#  $Id$
#
#  This file is part of ICTK.
#
#  ICTK is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  ICTK is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with ICTK; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


$template = "testTemplate.txt";
$tagParserClass = "##ParserClass##";
$tagParserTestClass = "##ParserTestClass##";
$tagEventClass = "##EventClass##";

if (@ARGV < 2) {
   printf "Usage: makeTest.pl <parserType> <eventType>\n";
   printf "   ex: makeTest.pl Shout Channel\n";
   exit(1);
}
else {
   ($parserClass, $eventClass) = @ARGV;
   $parserClass = "FICS" . $parserClass . "EventParser";
   $parserTestClass = $parserClass . "Test";
   $eventClass = "ICS" . $eventClass . "Event";
}

$javaFile = $parserTestClass . ".java";

if (-e $javaFile) {
   print "Exists: $javaFile\n";
}
else {
   print "Writing: $javaFile\n";

   open (TEMPLATE, $template) or die "can't open $template";
   open (JAVAFILE, ">$javaFile") or die "can't open $javaFile";

   { 
      local $/;
      $_ = <TEMPLATE>;
      close (TEMPLATE);

      s/$tagParserClass/$parserClass/g;
      s/$tagParserTestClass/$parserTestClass/g;
      s/$tagEventClass/$eventClass/g;

      print JAVAFILE $_;
      close (JAVAFILE);
   }
}

$dataFile = "data/" . $parserTestClass . ".data";
if (-e $dataFile) {
   print "Exists: $dataFile\n";
}
else {
   print "Writing: $dataFile\n";
   open (DATAFILE, ">$dataFile") or die "can't create $dataFile";
   print DATAFILE "##$parserTestClass Data file\n";
   print DATAFILE "##\$" . "Id:" . "\$\n";
   close (DATAFILE);
}
