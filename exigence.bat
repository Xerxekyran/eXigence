@echo off

set APP_HOME=.

cd %APP_HOME%
set CP=%APP_HOME%\bin

REM # Set jars in classpath
REM for %%i in ("%APP_HOME%\dist\*.jar") do call :appendCP %%~si

REM # Set others libraries in classpath
for %%i in ("%APP_HOME%\lib\*.jar") do call :appendCP %%~si

REM # Launch the program ...
java -cp %CP% eXigence.Main
pause
goto :eof

REM # function to add a jar file to classpath
:appendCP
set CP=%CP%;%1
goto :eof