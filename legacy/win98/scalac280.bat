@echo off

rem Compile Scala 2.8.0 code with this as official compilers do not work easily.

set SCALA_LIB="C:\Progra~1\scala\scala-2.8.0.final\lib"

C:\Progra~2\Java\jdk1.5.0_22\bin\java.exe -classpath "%SCALA_LIB%\scala-library.jar;%SCALA_LIB%\scala-compiler.jar" scala.tools.nsc.Main -bootclasspath "%SCALA_LIB%\scala-library.jar" %1