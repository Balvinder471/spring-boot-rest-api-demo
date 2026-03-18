@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo  Spring Boot REST API - Build Binary
echo ============================================================
echo.

:: Validate BINARY_PATH
if "%BINARY_PATH%"=="" (
    echo ERROR: BINARY_PATH environment variable is not set.
    echo Usage:  set BINARY_PATH=C:\path\to\output ^& build-binary.bat
    exit /b 1
)

echo Binary output path: %BINARY_PATH%
echo.

:: Create output directory if it doesn't exist
if not exist "%BINARY_PATH%" (
    echo Creating output directory: %BINARY_PATH%
    mkdir "%BINARY_PATH%"
    if errorlevel 1 (
        echo ERROR: Failed to create directory %BINARY_PATH%
        exit /b 1
    )
)

:: Build the project using Maven wrapper
echo Building the project with Maven...
echo.

if exist "%~dp0mvnw.cmd" (
    call "%~dp0mvnw.cmd" clean package -DskipTests -f "%~dp0pom.xml"
) else (
    call mvn clean package -DskipTests -f "%~dp0pom.xml"
)

if errorlevel 1 (
    echo.
    echo ERROR: Maven build failed.
    exit /b 1
)

echo.
echo Build successful. Copying JAR to %BINARY_PATH%...

:: Find and copy the built JAR (exclude original non-repackaged jar)
set "JAR_FOUND=0"
for %%f in ("%~dp0target\spring-boot-rest-api-*.jar") do (
    echo %%~nxf | findstr /i "original" >nul
    if errorlevel 1 (
        copy /Y "%%f" "%BINARY_PATH%\"
        if errorlevel 1 (
            echo ERROR: Failed to copy %%~nxf to %BINARY_PATH%
            exit /b 1
        )
        echo Copied: %%~nxf
        set "JAR_FOUND=1"
    )
)

if "!JAR_FOUND!"=="0" (
    echo ERROR: No JAR file found in target directory.
    exit /b 1
)

echo.
echo ============================================================
echo  Build complete. Binary available at: %BINARY_PATH%
echo ============================================================

endlocal
exit /b 0
