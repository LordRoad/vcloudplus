@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem NT Service Install/Uninstall script
rem
rem Options
rem install                Install the service using VCloudPlus as service name.
rem                        Service is installed using default settings.
rem remove                 Remove the service from the System.
rem
rem name        (optional) If the second argument is present it is considered
rem                        to be new service name
rem
rem $Id: service.bat 1000718 2010-09-24 06:00:00Z mturk $
rem ---------------------------------------------------------------------------

set "SELF=%~dp0%service.bat"
set "CURRENT_DIR=%cd%"
if not "%VCLOUDPLUS_HOME%" == "" goto gotHome
set "VCLOUDPLUS_HOME=%cd%"
if exist "%VCLOUDPLUS_HOME%\bin\vcloudplus.jar" goto okHome
rem CD to the upper dir
cd ..
set "VCLOUDPLUS_HOME=%cd%"
if exist "%VCLOUDPLUS_HOME%\bin\vcloudplus.jar" goto okHome
echo The vcloudplus.jar was not found...
echo The VCLOUDPLUS_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program
goto end
rem path is correct
:okHome
rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto gotJdkHome
if not "%JRE_HOME%" == "" goto gotJreHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo Service will try to guess them from the registry.
goto okJavaHome
:gotJreHome
if not exist "%JRE_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJavaHome
goto okJavaHome
:gotJdkHome
if not exist "%JAVA_HOME%\jre\bin\java.exe" goto noJavaHome
if not exist "%JAVA_HOME%\jre\bin\javaw.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javac.exe" goto noJavaHome
if not "%JRE_HOME%" == "" goto okJavaHome
set "JRE_HOME=%JAVA_HOME%\jre"
goto okJavaHome
:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
echo NB: JAVA_HOME should point to a JDK not a JRE
goto end
:okJavaHome
if not "%VCLOUDPLUS_BASE%" == "" goto gotBase
set "VCLOUDPLUS_BASE=%VCLOUDPLUS_HOME%"
:gotBase

set "EXECUTABLE=%VCLOUDPLUS_HOME%\bin\vpsvr.exe"
rem Set default Service name
set SERVICE_NAME=VCloudPlus

if "x%1x" == "xx" goto displayUsage
set SERVICE_CMD=%1
shift
if "x%1x" == "xx" goto checkServiceCmd
:checkUser
if "x%1x" == "x/userx" goto runAsUser
if "x%1x" == "x--userx" goto runAsUser
set SERVICE_NAME=%1
set PR_DISPLAYNAME=Apache Tomcat %1
shift
if "x%1x" == "xx" goto checkServiceCmd
goto checkUser
:runAsUser
shift
if "x%1x" == "xx" goto displayUsage
set SERVICE_USER=%1
shift
runas /env /savecred /user:%SERVICE_USER% "%COMSPEC% /K \"%SELF%\" %SERVICE_CMD% %SERVICE_NAME%"
goto end
:checkServiceCmd
if /i %SERVICE_CMD% == install goto doInstall
if /i %SERVICE_CMD% == remove goto doRemove
if /i %SERVICE_CMD% == uninstall goto doRemove
echo Unknown parameter "%1"
:displayUsage
echo.
echo Usage: service.bat install/remove [service_name] [/user username]
goto end

:doRemove
rem Remove the service
"%EXECUTABLE%" //DS//%SERVICE_NAME%
if not errorlevel 1 goto removed
echo Failed removing '%SERVICE_NAME%' service
goto end
:removed
echo The service '%SERVICE_NAME%' has been removed
goto end

:doInstall
rem Install the service
echo Installing the service '%SERVICE_NAME%' ...
echo Using VCLOUDPLUS_HOME:    "%VCLOUDPLUS_HOME%"
echo Using VCLOUDPLUS_BASE:	"%VCLOUDPLUS_BASE%"
echo Using JAVA_HOME:        "%JAVA_HOME%"
echo Using JRE_HOME:         "%JRE_HOME%"

set PR_DESCRIPTION=VCloudPlus extension of vcloud management
set "PR_INSTALL=%EXECUTABLE%"
set "PR_LOGPATH=%VCLOUDPLUS_BASE%\log"
rem now there is no other dependent
set "PR_CLASSPATH=%VCLOUDPLUS_HOME%\bin\vcloudplus.jar"
rem Set the server jvm from JAVA_HOME
set "PR_JVM=%JRE_HOME%\bin\server\jvm.dll"
if exist "%PR_JVM%" goto foundJvm
rem Set the client jvm from JAVA_HOME
set "PR_JVM=%JRE_HOME%\bin\client\jvm.dll"
if exist "%PR_JVM%" goto foundJvm
set PR_JVM=auto
:foundJvm
echo Using JVM:              "%PR_JVM%"

rem using static start and stop method as service entrance 
rem "%EXECUTABLE%" //IS//%SERVICE_NAME% --StartClass org.artemis.vcloudplus.en.VCloudPlusDaemon --StopClass org.artemis.vcloudplus.en.VCloudPlusDaemon --StartMethod windowsStart --StopMethod windowsStop
"%EXECUTABLE%" //IS//%SERVICE_NAME% --StartClass org.artemis.vcloudplus.en.VCloudPlusDaemon --StopClass org.artemis.vcloudplus.en.VCloudPlusDaemon --StartParams start --StopParams stop
if not errorlevel 1 goto installed
echo Failed installing '%SERVICE_NAME%' service
goto end
:installed
rem Clear the environment variables. They are not needed any more.
set PR_DISPLAYNAME=
set PR_DESCRIPTION=
set PR_INSTALL=
set PR_LOGPATH=
set PR_CLASSPATH=
set PR_JVM=
rem Set extra parameters
"%EXECUTABLE%" //US//%SERVICE_NAME% --Startup auto --StartMode jvm --StopMode jvm
rem More extra parameters
set "PR_LOGPATH=%VCLOUDPLUS_BASE%\log"
set PR_STDOUTPUT=auto
set PR_STDERROR=auto
"%EXECUTABLE%" //US//%SERVICE_NAME% ++JvmOptions ";-Dvcplog.home=%VCLOUDPLUS_BASE%\log;-Dlog4j.configuration=file:%VCLOUDPLUS_BASE%/config/log4j.properties;-Dvcloudplus.properties=%VCLOUDPLUS_BASE%/config/vcloudplus.properties;-Dorg.quartz.properties=%VCLOUDPLUS_BASE%/config/quartz_node.properties" --JvmMs 32 --JvmMx 128
echo The service '%SERVICE_NAME%' has been installed.


:end
cd "%CURRENT_DIR%"
