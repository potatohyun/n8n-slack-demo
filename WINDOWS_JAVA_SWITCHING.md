# Windows에서 Java 버전 전환 가이드

Windows 환경에서 회사 프로젝트(Java 8)와 개인 프로젝트(최신 Java)를 쉽게 전환하는 방법입니다.

## 추천 방법 1: Chocolatey + 환경 변수 스크립트

### 1. Chocolatey로 Java 설치

```powershell
# 관리자 권한 PowerShell에서 실행
# Chocolatey 설치 (없는 경우)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Java 8 설치
choco install openjdk8 -y

# 최신 Java 설치 (예: Java 21)
choco install openjdk21 -y
```

### 2. 버전 전환 스크립트 생성

**`switch-java8.ps1`** 파일 생성:
```powershell
# Java 8로 전환
$java8Path = "C:\Program Files\Java\jdk1.8.0_xxx"  # 실제 경로로 변경
[Environment]::SetEnvironmentVariable("JAVA_HOME", $java8Path, "User")
$env:JAVA_HOME = $java8Path
$env:PATH = "$java8Path\bin;" + ($env:PATH -replace [regex]::Escape("$java8Path\bin;"), "")
Write-Host "Switched to Java 8" -ForegroundColor Green
java -version
```

**`switch-java21.ps1`** 파일 생성:
```powershell
# Java 21로 전환
$java21Path = "C:\Program Files\Java\jdk-21"  # 실제 경로로 변경
[Environment]::SetEnvironmentVariable("JAVA_HOME", $java21Path, "User")
$env:JAVA_HOME = $java21Path
$env:PATH = "$java21Path\bin;" + ($env:PATH -replace [regex]::Escape("$java21Path\bin;"), "")
Write-Host "Switched to Java 21" -ForegroundColor Green
java -version
```

**사용법:**
```powershell
.\switch-java8.ps1   # Java 8로 전환
.\switch-java21.ps1  # Java 21로 전환
```

## 추천 방법 2: SDKMAN (Git Bash 또는 WSL 사용)

Git Bash가 설치되어 있다면 SDKMAN을 사용할 수 있습니다:

```bash
# Git Bash에서 실행
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Java 설치
sdk install java 8.0.392-tem
sdk install java 21.0.1-tem

# 버전 전환
sdk use java 8.0.392-tem
sdk use java 21.0.1-tem
```

## 추천 방법 3: 프로젝트별 배치 파일

각 프로젝트 루트에 배치 파일을 두는 방법입니다.

**업무 프로젝트에 `use-java8.bat`**:
```batch
@echo off
setx JAVA_HOME "C:\Program Files\Java\jdk1.8.0_xxx"
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_xxx
set PATH=%JAVA_HOME%\bin;%PATH%
echo Switched to Java 8
java -version
cmd /k
```

**개인 프로젝트에 `use-java21.bat`**:
```batch
@echo off
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
echo Switched to Java 21
java -version
cmd /k
```

**사용법:** 프로젝트 폴더에서 해당 배치 파일을 더블클릭하면 새 터미널이 열리며 해당 Java 버전이 적용됩니다.

## 추천 방법 4: IDE 설정 활용

### IntelliJ IDEA
- **File → Project Structure → Project → SDK**에서 프로젝트별 Java 버전 설정
- 각 프로젝트마다 다른 SDK를 지정하면 자동으로 해당 버전 사용

### Eclipse
- **Project → Properties → Java Build Path → Libraries**에서 JRE 버전 변경
- **Window → Preferences → Java → Installed JREs**에서 여러 버전 관리

## 실용적인 통합 솔루션

더 편하게 사용하려면 전환 스크립트를 하나로 통합할 수 있습니다:

**`switch-java.ps1`**:
```powershell
param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("8", "21")]
    [string]$Version
)

$javaPaths = @{
    "8" = "C:\Program Files\Java\jdk1.8.0_xxx"  # 실제 경로로 변경
    "21" = "C:\Program Files\Java\jdk-21"        # 실제 경로로 변경
}

$javaPath = $javaPaths[$Version]
if (Test-Path $javaPath) {
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath, "User")
    $env:JAVA_HOME = $javaPath
    
    # PATH에서 기존 Java 제거 후 새로 추가
    $oldPath = [Environment]::GetEnvironmentVariable("PATH", "User")
    $newPath = "$javaPath\bin;" + ($oldPath -replace "C:\\Program Files\\Java\\[^;]+\\bin;", "")
    [Environment]::SetEnvironmentVariable("PATH", $newPath, "User")
    $env:PATH = "$javaPath\bin;$oldPath"
    
    Write-Host "Switched to Java $Version" -ForegroundColor Green
    java -version
} else {
    Write-Host "Java $Version not found at $javaPath" -ForegroundColor Red
}
```

**사용법:**
```powershell
.\switch-java.ps1 -Version 8   # Java 8로 전환
.\switch-java.ps1 -Version 21  # Java 21로 전환
```

## 설치 경로 확인 방법

실제 Java 설치 경로를 확인하려면:

```powershell
# PowerShell에서
dir "C:\Program Files\Java\"

# 또는
Get-ChildItem "C:\Program Files\Java\"
```

일반적인 설치 경로:
- Java 8: `C:\Program Files\Java\jdk1.8.0_xxx` 또는 `C:\Program Files\Eclipse Adoptium\jdk-8.0.xxx.x-hotspot`
- Java 21: `C:\Program Files\Java\jdk-21` 또는 `C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot`

## 현재 Java 버전 확인

```powershell
# PowerShell에서
java -version
javac -version
echo $env:JAVA_HOME

# CMD에서
java -version
javac -version
echo %JAVA_HOME%
```

## 주의사항

1. **새 터미널 필요**: 환경 변수 변경 후 **새 PowerShell/CMD 창을 열어야** 적용됩니다.
2. **IDE 재시작**: IDE를 사용 중이면 **재시작**해야 합니다.
3. **실제 경로 확인**: 스크립트의 Java 경로를 실제 설치 경로로 변경해야 합니다.
4. **관리자 권한**: 일부 환경 변수 설정은 관리자 권한이 필요할 수 있습니다.

## 추천 워크플로우

1. **회사 프로젝트 시작 시:**
   - `switch-java8.ps1` 실행 또는 `use-java8.bat` 더블클릭
   - 새 터미널 열기
   - IDE에서 프로젝트 열기

2. **개인 프로젝트 시작 시:**
   - `switch-java21.ps1` 실행 또는 `use-java21.bat` 더블클릭
   - 새 터미널 열기
   - IDE에서 프로젝트 열기

3. **가장 간단한 방법:**
   - 각 프로젝트 루트에 배치 파일(`use-java8.bat`, `use-java21.bat`) 생성
   - 프로젝트 작업 시작 전 해당 배치 파일 실행

## 문제 해결

### Java 버전이 변경되지 않는 경우
- 새 터미널 창을 열어보세요
- IDE를 완전히 종료 후 재시작하세요
- 시스템 환경 변수에서 JAVA_HOME과 PATH를 직접 확인하세요

### 권한 오류가 발생하는 경우
- PowerShell을 **관리자 권한으로 실행**하세요
- `Set-ExecutionPolicy RemoteSigned -Scope CurrentUser` 실행 후 다시 시도

### 여러 Java가 설치되어 혼란스러운 경우
- 불필요한 Java 버전은 제거하거나
- IDE에서만 프로젝트별로 다른 버전을 사용하는 방법(방법 4)을 추천합니다

