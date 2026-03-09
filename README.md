# Spring AI MCP 예제 프로젝트

## 프로젝트 구조

```
spring-ai/
├── mcp-server/   # MCP Tool 서버 (port: 8081)
└── mcp-client/   # MCP 클라이언트 + REST API (port: 8080)
```

---

## 동작 원리

### 전체 흐름

```
사용자
  │  GET /api/chat/weather?city=Seoul
  ▼
ChatController (mcp-client :8080)
  │  chatClient.prompt("What is the current weather in Seoul?").call()
  ▼
OpenAI API (GPT-4o)
  │  Tool 목록 확인 후 → getCurrentWeather(city="Seoul") 호출 결정
  ▼
Spring AI MCP Client
  │  SSE (Server-Sent Events) 통신
  ▼
MCP Server (:8081)  →  WeatherTools.getCurrentWeather("Seoul")
  │  "Seoul: 15°C, Partly Cloudy, Humidity 60%"
  ▼
OpenAI API (GPT-4o)
  │  Tool 결과를 받아 최종 자연어 응답 생성
  ▼
사용자  ←  "현재 서울의 날씨는 15°C이며 부분적으로 흐립니다..."
```

### 앱 시작 시 Tool 등록 흐름

```
[mcp-server 시작]
McpServerConfig
  └─ MethodToolCallbackProvider
       └─ WeatherTools의 @Tool 메서드 스캔
            ├─ getCurrentWeather  → MCP Tool로 등록 (name, description, parameter schema 생성)
            └─ getWeatherForecast → MCP Tool로 등록

[mcp-client 시작]
SSE 연결 (http://localhost:8081/mcp/sse)
  └─ 서버로부터 Tool 목록 수신
McpClientConfig
  └─ ChatClient에 Tool 목록 등록 (defaultToolCallbacks)
```

### Tool 호출 상세 흐름

```
① ChatClient → OpenAI: 사용자 메시지 + Tool 스펙 목록 전송
   Tool 스펙 예시:
   {
     "name": "getCurrentWeather",
     "description": "Get current weather information for a given city",
     "parameters": { "city": { "type": "string" } }
   }

② OpenAI → ChatClient: Tool 호출 요청 반환
   { "tool_call": { "name": "getCurrentWeather", "arguments": { "city": "Seoul" } } }

③ Spring AI MCP Client → MCP Server: SSE로 Tool 실행 요청
   POST http://localhost:8081/mcp/message

④ WeatherTools.getCurrentWeather("Seoul") 실행
   → "Seoul: 15°C, Partly Cloudy, Humidity 60%"

⑤ ChatClient → OpenAI: Tool 실행 결과 전달

⑥ OpenAI → 사용자: 최종 자연어 응답 생성
```

---

## 실행 방법

### 사전 준비

OpenAI API Key 발급 후 환경변수 설정:
```bash
export OPENAI_API_KEY=sk-...
```

### 1. mcp-server 실행 (먼저 실행)

```bash
./gradlew :mcp-server:bootRun
```

서버 시작 확인:
```
Started McpServerApplication on port 8081
```

### 2. mcp-client 실행 (별도 터미널)

```bash
./gradlew :mcp-client:bootRun
```

클라이언트 시작 확인:
```
Started McpClientApplication on port 8080
```

---

## API 사용

### 날씨 조회 API

특정 도시의 현재 날씨 + 3일 예보를 조회합니다.

```bash
curl "http://localhost:8080/api/chat/weather?city=Seoul"
curl "http://localhost:8080/api/chat/weather?city=Tokyo"
curl "http://localhost:8080/api/chat/weather?city=London"
```

지원 도시: `Seoul`, `Busan`, `Jeju`, `New York`, `Tokyo`, `London`

### 자유 질문 API

OpenAI + MCP Tool을 활용한 자유 질문입니다.

```bash
curl "http://localhost:8080/api/chat?message=What+is+the+weather+in+Busan?"
curl "http://localhost:8080/api/chat?message=Compare+the+weather+in+Seoul+and+Tokyo"
curl "http://localhost:8080/api/chat?message=Give+me+a+5-day+forecast+for+Jeju"
```

---

## 핵심 개념 정리

| 구성 요소 | 역할 |
|---|---|
| `@Tool` | 메서드를 MCP Tool로 선언. `description`은 GPT가 언제 호출할지 판단하는 근거 |
| `@ToolParam` | Tool 파라미터 설명. GPT가 올바른 인자를 추출하는 데 사용 |
| `MethodToolCallbackProvider` | `@Tool` 메서드를 스캔해 MCP 프로토콜로 노출 |
| `SSE (Server-Sent Events)` | mcp-client ↔ mcp-server 간 실시간 통신 프로토콜 |
| `ChatClient.defaultToolCallbacks` | 모든 요청에 MCP Tool을 자동으로 사용 가능하게 등록 |

### WeatherTools가 직접 HTTP로 노출되지 않는 이유

`WeatherTools`는 `@RestController`가 아닌 `@Component`로 선언되어 있습니다.
MCP 프로토콜(SSE)을 통해서만 접근 가능하며, **mcp-client는 Tool의 구현 코드를 전혀 모릅니다.**
오직 Tool의 이름, 파라미터 타입, description만 알고 있으며, GPT가 이를 보고 호출 여부를 결정합니다.

