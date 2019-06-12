@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  notification startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and NOTIFICATION_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\notification-1.0-SNAPSHOT.jar;%APP_HOME%\lib\ktor-server-netty-1.2.0.jar;%APP_HOME%\lib\ktor-client-1.2.1.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\firebase-admin-6.8.1.jar;%APP_HOME%\lib\ktor-server-host-common-1.2.0.jar;%APP_HOME%\lib\ktor-server-core-1.2.0.jar;%APP_HOME%\lib\ktor-client-core-jvm-1.2.1.jar;%APP_HOME%\lib\ktor-http-cio-jvm-1.2.1.jar;%APP_HOME%\lib\ktor-http-jvm-1.2.1.jar;%APP_HOME%\lib\ktor-network-1.2.1.jar;%APP_HOME%\lib\ktor-utils-jvm-1.2.1.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.3.31.jar;%APP_HOME%\lib\kotlin-reflect-1.3.31.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.3.31.jar;%APP_HOME%\lib\kotlinx-coroutines-io-jvm-0.1.8.jar;%APP_HOME%\lib\kotlinx-io-jvm-0.1.8.jar;%APP_HOME%\lib\atomicfu-0.12.6.jar;%APP_HOME%\lib\kotlinx-coroutines-jdk8-1.2.1.jar;%APP_HOME%\lib\kotlinx-coroutines-core-1.2.1.jar;%APP_HOME%\lib\kotlin-stdlib-1.3.31.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\config-1.3.1.jar;%APP_HOME%\lib\netty-codec-http2-4.1.24.Final.jar;%APP_HOME%\lib\alpn-api-1.1.3.v20160715.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\google-api-client-gson-1.25.0.jar;%APP_HOME%\lib\google-cloud-storage-1.43.0.jar;%APP_HOME%\lib\google-cloud-core-http-1.43.0.jar;%APP_HOME%\lib\google-api-client-1.25.0.jar;%APP_HOME%\lib\google-cloud-firestore-0.61.0-beta.jar;%APP_HOME%\lib\google-cloud-core-grpc-1.43.0.jar;%APP_HOME%\lib\gax-grpc-1.30.0.jar;%APP_HOME%\lib\google-cloud-core-1.43.0.jar;%APP_HOME%\lib\gax-httpjson-0.47.0.jar;%APP_HOME%\lib\gax-1.30.0.jar;%APP_HOME%\lib\google-auth-library-oauth2-http-0.11.0.jar;%APP_HOME%\lib\google-http-client-jackson2-1.25.0.jar;%APP_HOME%\lib\google-oauth-client-1.25.0.jar;%APP_HOME%\lib\google-http-client-gson-1.25.0.jar;%APP_HOME%\lib\google-http-client-appengine-1.24.1.jar;%APP_HOME%\lib\google-http-client-jackson-1.24.1.jar;%APP_HOME%\lib\google-http-client-1.25.0.jar;%APP_HOME%\lib\proto-google-cloud-firestore-v1beta1-0.26.0.jar;%APP_HOME%\lib\proto-google-iam-v1-0.12.0.jar;%APP_HOME%\lib\api-common-1.7.0.jar;%APP_HOME%\lib\opencensus-contrib-grpc-util-0.15.0.jar;%APP_HOME%\lib\opencensus-contrib-http-util-0.15.0.jar;%APP_HOME%\lib\grpc-netty-shaded-1.13.1.jar;%APP_HOME%\lib\grpc-stub-1.13.1.jar;%APP_HOME%\lib\grpc-auth-1.13.1.jar;%APP_HOME%\lib\grpc-protobuf-1.13.1.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.13.1.jar;%APP_HOME%\lib\grpc-core-1.13.1.jar;%APP_HOME%\lib\opencensus-contrib-grpc-metrics-0.12.3.jar;%APP_HOME%\lib\opencensus-api-0.15.0.jar;%APP_HOME%\lib\protobuf-java-util-3.6.0.jar;%APP_HOME%\lib\guava-20.0.jar;%APP_HOME%\lib\netty-codec-http-4.1.24.Final.jar;%APP_HOME%\lib\netty-handler-4.1.24.Final.jar;%APP_HOME%\lib\netty-codec-4.1.24.Final.jar;%APP_HOME%\lib\netty-transport-4.1.24.Final.jar;%APP_HOME%\lib\kotlinx-coroutines-io-0.1.8.jar;%APP_HOME%\lib\kotlinx-io-0.1.8.jar;%APP_HOME%\lib\kotlinx-coroutines-core-common-1.2.1.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.3.31.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\atomicfu-common-0.12.3.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\httpclient-4.5.5.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar;%APP_HOME%\lib\google-auth-library-credentials-0.11.0.jar;%APP_HOME%\lib\google-api-services-storage-v1-rev135-1.24.1.jar;%APP_HOME%\lib\auto-value-1.4.jar;%APP_HOME%\lib\netty-buffer-4.1.24.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.24.Final.jar;%APP_HOME%\lib\jackson-core-2.9.6.jar;%APP_HOME%\lib\gson-2.7.jar;%APP_HOME%\lib\httpcore-4.4.9.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\joda-time-2.9.2.jar;%APP_HOME%\lib\proto-google-common-protos-1.12.0.jar;%APP_HOME%\lib\protobuf-java-3.6.0.jar;%APP_HOME%\lib\grpc-context-1.13.1.jar;%APP_HOME%\lib\threetenbp-1.3.3.jar;%APP_HOME%\lib\netty-common-4.1.24.Final.jar;%APP_HOME%\lib\jackson-core-asl-1.9.11.jar;%APP_HOME%\lib\error_prone_annotations-2.1.2.jar

@rem Execute notification
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %NOTIFICATION_OPTS%  -classpath "%CLASSPATH%" com.example.kotlinserver.AppKt %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable NOTIFICATION_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%NOTIFICATION_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
