@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo  Spring Boot REST API - Create Manual Deployable Package
echo ============================================================
echo.

:: Validate BINARY_PATH
if "%BINARY_PATH%"=="" (
    echo ERROR: BINARY_PATH environment variable is not set.
    echo Usage:  set BINARY_PATH=C:\path\to\jar ^& set PACKAGE_PATH=C:\path\to\output ^& create-manual-package.bat
    exit /b 1
)

:: Validate PACKAGE_PATH
if "%PACKAGE_PATH%"=="" (
    echo ERROR: PACKAGE_PATH environment variable is not set.
    echo Usage:  set BINARY_PATH=C:\path\to\jar ^& set PACKAGE_PATH=C:\path\to\output ^& create-manual-package.bat
    exit /b 1
)

echo Binary path (JAR location): %BINARY_PATH%
echo Package path (ZIP output) : %PACKAGE_PATH%
echo.

:: Locate the JAR file in BINARY_PATH
set "JAR_FILE="
for %%f in ("%BINARY_PATH%\spring-boot-rest-api-*.jar") do (
    echo %%~nxf | findstr /i "original" >nul
    if errorlevel 1 (
        set "JAR_FILE=%%f"
    )
)

if "%JAR_FILE%"=="" (
    echo ERROR: No JAR file found in %BINARY_PATH%
    exit /b 1
)

echo Found JAR: %JAR_FILE%
echo.

:: Create PACKAGE_PATH if it doesn't exist
if not exist "%PACKAGE_PATH%" (
    echo Creating package output directory: %PACKAGE_PATH%
    mkdir "%PACKAGE_PATH%"
    if errorlevel 1 (
        echo ERROR: Failed to create directory %PACKAGE_PATH%
        exit /b 1
    )
)

:: Set up staging area
set "STAGE_DIR=%PACKAGE_PATH%\staging"
set "PKG_DIR=%STAGE_DIR%\RestApiDemo"

:: Clean previous staging area
if exist "%STAGE_DIR%" (
    echo Cleaning previous staging area...
    rmdir /s /q "%STAGE_DIR%"
)

:: Create package directory structure
echo Creating package structure...
mkdir "%PKG_DIR%\Documents"
mkdir "%PKG_DIR%\WebApp"

:: Copy Documents folder
echo Copying installation documents...
set "DOCS_DIR=%~dp0Documents"
if not exist "%DOCS_DIR%" (
    echo ERROR: Documents folder not found at %DOCS_DIR%
    exit /b 1
)
xcopy /s /e /y "%DOCS_DIR%\*" "%PKG_DIR%\Documents\" >nul
if errorlevel 1 (
    echo ERROR: Failed to copy documents.
    exit /b 1
)
echo Documents copied successfully.

:: Extract JAR into WebApp (exploded form)
echo Extracting JAR into WebApp (exploded form)...
pushd "%PKG_DIR%\WebApp"
jar -xf "%JAR_FILE%"
if errorlevel 1 (
    popd
    echo ERROR: Failed to extract JAR file. Ensure 'jar' command is available (JDK required).
    exit /b 1
)
popd
echo JAR extracted successfully.

:: Create the ZIP package
echo.
echo Creating deployable ZIP package...
set "ZIP_FILE=%PACKAGE_PATH%\RestApiDemo.zip"

:: Remove old zip if it exists
if exist "%ZIP_FILE%" del /f "%ZIP_FILE%"

:: Use PowerShell to create the zip (available on all modern Windows)
powershell -NoProfile -Command "Compress-Archive -Path '%PKG_DIR%' -DestinationPath '%ZIP_FILE%' -Force"
if errorlevel 1 (
    echo ERROR: Failed to create ZIP package.
    exit /b 1
)

:: Clean up staging area
echo Cleaning up staging area...
rmdir /s /q "%STAGE_DIR%"

echo.
echo ============================================================
echo  Package created successfully!
echo  Location: %ZIP_FILE%
echo.
echo  Package contents:
echo    RestApiDemo/
echo      Documents/            - Installation documentation
echo      WebApp/               - Application (exploded JAR)
echo ============================================================

endlocal
exit /b 0
