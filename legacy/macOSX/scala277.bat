@echo off

rem ##########################################################################
rem #
rem # This is free software; see the distribution for copying conditions.
rem # There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A
rem # PARTICULAR PURPOSE.
rem #
rem # MODIFIED to run by Thomas Pearson for 13SWE project management.
rem #
rem ##########################################################################

rem We adopt the following conventions:
rem - System/user environment variables start with a letter
rem - Local batch variables start with an underscore ('_')

rem My new working command.
set SCALA_LIB="C:\Progra~1\scala\scala-2.7.7.final\lib"

C:\Progra~2\Java\jdk1.5.0_19\bin\java.exe -classpath ".;%SCALA_LIB%\scala-library.jar;%SCALA_LIB%\scala-compiler.jar" %1