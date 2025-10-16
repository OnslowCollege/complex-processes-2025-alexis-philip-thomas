@echo off

rem Configuration.
set JAVA_HOME=C:\Progra~2\Java\jdk1.5.0_22
set SCALA_HOME=C:\Progra~1\scala\scala-2.8.0.final
set SCALA_LIB=%SCALA_HOME%\lib

set SRC_FILE=%1
set MAIN_CLASS=%2

rem Compile Scala 2.8.0 source code.
%JAVA_HOME%\bin\java.exe -classpath "%SCALA_LIB%\scala-library.jar;%SCALA_LIB%\scala-compiler.jar" scala.tools.nsc.Main -bootclasspath "%SCALA_LIB%\scala-library.jar" %SRC_FILE%

rem Build manifest.
echo Main-Class: %MAIN_CLASS%> manifest.txt
echo Class-Path: scala-library.jar>> manifest.txt

rem Package classes to .jar.
set JAR_NAME=%MAIN_CLASS%.jar
%JAVA_HOME%\bin\jar.exe cfm "%JAR_NAME%" manifest.txt *.class