@echo off

rem Compile Scala 2.7.7 code with this as official compilers do not work easily.

set SCALA_LIB="C:\Progra~1\scala\scala-2.7.7.final\lib"
set RTJAR="C:\Progra~2\Java\jdk1.5.0_19\jre\lib\rt.jar"

C:\Progra~2\Java\jdk1.5.0_19\bin\java.exe -classpath "%SCALA_LIB%\scala-library.jar;%SCALA_LIB%\scala-compiler.jar" scala.tools.nsc.Main -bootclasspath "%RTJAR%;%SCALA_LIB%\scala-library.jar" %1