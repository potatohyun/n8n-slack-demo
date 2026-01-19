# n8n Slack 크롤링 워크플로우

n8n을 사용하여 Slack의 채널, 메시지, 사용자 정보를 정기적으로 크롤링하고 JSON 파일로 저장하는 프로젝트입니다.

## 기능

- **정기 크롤링**: 스케줄 트리거를 통한 자동 실행 (기본: 매일 실행)
- **데이터 수집**: Slack API를 통한 채널, 메시지, 사용자 정보 수집
- **데이터 저장**: 타임스탬프가 포함된 JSON 파일로 저장
- **Docker 기반**: Docker Compose를 통한 간편한 실행

## 사전 요구사항

- Docker 및 Docker Compose 설치
- Slack Workspace 관리자 권한 또는 앱 생성 권한
- Slack Bot Token

## 설정 방법

### 1. Slack 앱 생성 및 토큰 발급

1. [Slack API 페이지](https://api.slack.com/apps)에 접속
2. "Create New App" 클릭 → "From scratch" 선택
3. 앱 이름과 워크스페이스 선택
4. **OAuth & Permissions** 메뉴로 이동
5. **Scopes** 섹션에서 다음 권한 추가:
   - `channels:read` - 채널 목록 조회
   - `channels:history` - 채널 메시지 조회
   - `groups:read` - 비공개 채널 목록 조회
   - `groups:history` - 비공개 채널 메시지 조회
   - `users:read` - 사용자 정보 조회
   - `users:read.email` - 사용자 이메일 조회 (선택사항)
6. **Bot Token Scopes**에 위 권한들이 추가되었는지 확인
7. 페이지 상단의 **Install to Workspace** 클릭하여 워크스페이스에 설치
8. 설치 완료 후 **Bot User OAuth Token** (xoxb-로 시작) 복사

### 2. 환경 변수 설정

`.env.example` 파일을 참고하여 `.env` 파일을 생성합니다:

```bash
# .env 파일 생성
cp .env.example .env
```

`.env` 파일을 열어 다음 내용을 수정합니다:

```env
# n8n 기본 인증 설정
N8N_USER=admin
N8N_PASSWORD=your-secure-password-here

# Slack API 토큰
SLACK_BOT_TOKEN=xoxb-your-actual-slack-bot-token-here
```

### 3. Docker 컨테이너 실행

```bash
# 컨테이너 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f n8n
```

n8n 웹 인터페이스는 `http://localhost:5678`에서 접근할 수 있습니다.

### 4. n8n 워크플로우 설정

1. 웹 브라우저에서 `http://localhost:5678` 접속
2. `.env` 파일에 설정한 사용자명과 비밀번호로 로그인
3. 좌측 메뉴에서 **Workflows** 클릭
4. **Import from File** 또는 **Import from URL**을 사용하여 `workflows/slack-crawler.json` 파일을 가져오기
5. 워크플로우가 열리면 각 Slack 노드에서 **Credential** 설정:
   - **Get Channels**, **Get Messages**, **Get Users** 노드 클릭
   - **Credential** 드롭다운에서 **Create New** 선택
   - **Slack OAuth2 API** 선택
   - **Access Token**에 Slack Bot Token 입력
   - **Save** 클릭
6. 워크플로우 저장 및 활성화

### 5. 스케줄 설정 (선택사항)

기본적으로 워크플로우는 매일 실행되도록 설정되어 있습니다. 실행 주기를 변경하려면:

1. **Schedule Trigger** 노드 클릭
2. **Rule** 섹션에서 원하는 스케줄 설정
   - 예: 매일 오전 2시 → `0 2 * * *`
   - 예: 매주 월요일 오전 9시 → `0 9 * * 1`
   - 예: 매시간 → `0 * * * *`
3. **Save** 클릭

## 사용 방법

### 워크플로우 수동 실행

1. n8n 웹 인터페이스에서 워크플로우 선택
2. 우측 상단의 **Execute Workflow** 버튼 클릭
3. 실행 결과 확인

### 크롤링 결과 확인

크롤링된 데이터는 `data/` 디렉토리에 JSON 파일로 저장됩니다:

```bash
# 데이터 디렉토리 확인
ls data/

# 최신 파일 내용 확인 (예시)
cat data/slack-data-2024-01-01T02-00-00.000Z.json | jq .
```

### JSON 파일 구조

```json
{
  "crawledAt": "2024-01-01T02:00:00.000Z",
  "summary": {
    "totalChannels": 10,
    "totalMessages": 150,
    "totalUsers": 25
  },
  "channels": [
    {
      "id": "C1234567890",
      "name": "general",
      "isPrivate": false,
      "isArchived": false,
      "created": 1234567890,
      "numMembers": 20,
      "messages": [
        {
          "ts": "1234567890.123456",
          "text": "메시지 내용",
          "user": "U1234567890",
          "type": "message",
          "subtype": null,
          "attachments": []
        }
      ]
    }
  ],
  "users": [
    {
      "id": "U1234567890",
      "name": "john.doe",
      "realName": "John Doe",
      "displayName": "John",
      "email": "john@example.com",
      "isBot": false,
      "isAdmin": false,
      "deleted": false
    }
  ]
}
```

## 문제 해결

### Slack API 에러

- **"missing_scope"**: Slack 앱에 필요한 권한이 없는 경우
  - 해결: Slack 앱 설정에서 필요한 권한 추가 후 재설치

- **"not_authed"**: 토큰이 잘못되었거나 만료된 경우
  - 해결: `.env` 파일의 `SLACK_BOT_TOKEN` 확인 및 업데이트

### n8n 연결 문제

- **워크플로우가 실행되지 않음**: 
  - 워크플로우가 활성화되어 있는지 확인
  - Schedule Trigger 설정 확인

- **파일 저장 실패**:
  - `data/` 디렉토리 권한 확인
  - Docker 볼륨 마운트 확인

## 중지 및 정리

```bash
# 컨테이너 중지
docker-compose down

# 컨테이너 및 볼륨 삭제 (주의: 데이터가 삭제됩니다)
docker-compose down -v
```

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.
