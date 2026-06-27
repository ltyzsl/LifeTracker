@echo off
echo ================================================
echo  LifeTracker APK 构建脚本
echo   (需要先安装 Android Studio)
echo ================================================
echo.
echo 正在检查环境...
echo.

REM 设置 SDK 路径（Android Studio 默认安装路径）
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
if not exist "%ANDROID_HOME%" (
    echo [错误] 未找到 Android SDK
    echo 请先安装 Android Studio: https://developer.android.com/studio
    echo.
    pause
    exit /b 1
)

echo [OK] 找到 Android SDK: %ANDROID_HOME%
echo.

REM 设置 local.properties
echo sdk.dir=%ANDROID_HOME:\=\\% > local.properties
echo [OK] 已创建 local.properties
echo.

REM 构建 APK
echo 正在构建 APK (第一次需要下载依赖，请耐心等待)...
echo.
call gradlew.bat assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [错误] 构建失败，请检查上面的错误信息
    pause
    exit /b 1
)

echo.
echo ================================================
echo 构建成功！
echo APK 文件位置:
echo   app\build\outputs\apk\debug\app-debug.apk
echo ================================================
echo.
pause
