# DESIGN.md

## 현재 구현

이 프로젝트는 Java 17 Maven 애플리케이션이다. DC Inside 갤러리 목록의 최신 일반 게시글을 확인하고, 새 게시글이면 제목·URL·본문 텍스트를 디스코드 채널로 보낸다.

## 구성 요소

| 구성 요소 | 역할 |
| --- | --- |
| `Application` | 봇 시작, 설정 로드, 타이머 등록을 순서대로 실행 |
| `DiscordBot` | JDA 연결, 명령 이벤트 수신, 메시지 전송 |
| `SlashCommandFunctions` | `/cw` 명령과 `w-add`, `w-edit`, `w-del`, `w-lu` 하위 명령 등록 및 처리 |
| `WebpageConfig` | 채널 ID, 설정 이름, 대상 URL, 이미지 표시 옵션, 크롤링 주기, 마지막 확인 URL 보관 |
| `WebpageConfigs` | 설정 목록을 정적 `ArrayList`에 보관하고 JSON으로 읽고 쓰기 |
| `CrawlingTimer` | 1·5·30·60초마다 해당 주기의 설정을 선택해 크롤링 |
| `CrawlingGallery` | Jsoup으로 목록과 게시글을 읽고 전송할 텍스트 작성 |

## 봇 토큰 설정

`DiscordBot`은 시작할 때 `DISCORD_BOT_TOKEN` 환경 변수를 읽는다. 값이 비어 있으면 예외를 발생시키고, 값이 있으면 JDA 연결에 사용한다.

## 게시글 처리 흐름

1. `CrawlingTimer`가 설정의 `crawlingCycle`과 일치하는 주기의 작업을 실행한다.
2. `CrawlingGallery`가 설정된 목록 URL을 요청해 공지 항목을 제외한 첫 게시글의 URL을 선택한다.
3. 선택한 URL이 `latestCrawledUrl`과 같으면 메시지를 만들지 않는다.
4. 새 URL이면 `latestCrawledUrl`을 바꾸고 `src/main/resources/webpageConfigs.json`에 저장한다.
5. 게시글 페이지에서 제목과 `div.write_div`의 텍스트를 읽는다. 메시지가 2,000자를 넘으면 앞부분 2,000자만 사용한다.
6. `DiscordBot`이 JDA의 비동기 `queue()`로 설정된 채널에 메시지를 보낸다.

## 설정과 저장

- `WebpageConfigs`는 JSON 파일을 실행 작업 디렉터리 기준 상대 경로로 읽고 쓴다.
- 슬래시 명령의 추가·수정·삭제는 메모리의 설정 목록을 변경하고 JSON 파일에 저장한다.
- `imgVisibility`는 입력 및 저장되지만 현재 크롤링과 메시지 생성에는 사용되지 않는다.
