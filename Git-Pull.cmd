::#!
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: {{{1 :::::::^::::
:: Copyright © 2013 Martin Krischik
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: $Author$
:: $Revision$
:: $Date$
:: $Id$
:: $HeadURL$
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: }}}1 :::::::::::
@ECHO OFF

SETLOCAL
    SET PATH=%PATH%;C:\opt\Git\bin
    SET PATH=%PATH%;C:\opt\Scala\2.10.0\bin

    CALL scala -language:postfixOps -save %~f0 %*    
ENDLOCAL
    
GOTO :eof
::!#

import scala.sys.process._

"git" :: "pull" :: "https://github.com/JakeWharton/Android-ViewPagerIndicator.git"  :: Nil !;
"git" :: "pull" :: "https://github.com/krischik/Android-ViewPagerIndicator.git"	    :: Nil !;
"git" :: "push" :: Nil !;

// vim: set wrap tabstop=8 shiftwidth=4 softtabstop=4 noexpandtab :
// vim: set textwidth=0 filetype=scala foldmethod=marker nospell :
