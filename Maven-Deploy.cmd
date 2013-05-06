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

val M2_Home      = System.getenv ("M2_HOME")
val Maven_Deploy = System.getenv ("MAVEN_DEPLOY")
val Project_Name = System.getenv ("PROJECT_NAME")
val Maven_Name   = Project_Name +" Maven Repository"
val mvn          = if (System.getProperty ("os.name") contains "Windows")
    {
        "cmd" :: "/C" :: M2_Home + "\\bin\\mvn" :: Nil
    }
    else
    {
        "mvn" :: Nil
    }

mvn ::: "--activate-profiles" :: "release" :: "install" :: Nil !;
mvn ::: "--activate-profiles" :: "release" :: "site:site" :: Nil !;

mvn :::
    "--define" :: "repo.id="   + Project_Name ::
    "--define" :: "repo.name=" + Maven_Name   ::
    "--define" :: "repo.url="  + Maven_Deploy ::
    "deploy"   :: Nil !;

// vim: set wrap tabstop=8 shiftwidth=4 softtabstop=4 noexpandtab :
// vim: set textwidth=0 filetype=scala foldmethod=marker nospell :
