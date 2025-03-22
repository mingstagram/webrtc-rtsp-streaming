# 🧩 WebRTC RTSP 스트리밍 트러블슈팅 문서

## 📌개요
이 프로젝트는 **RTSP 스트리밍을 WebRTC로 변환**하여 **웹에서 실시간 CCTV 영상을 송출**하는 시스템입니다.
**Janus WebRTC Gateway**를 백엔드(Spring WebFlux)에서 연동하고, 프론트엔드는 **React**를 통해 실시간 영상 UI를 구현했습니다.

---

## ⚠️ 문제 목록 & 해결 방법
### 🔧 1. Janus plugin.streaming에서 RTSP URL 인식 오류
- 오류 로그:` [ERR] Can't add 'rtsp' stream, missing url...`
- 원인: `.jcfg`가 아닌 `.conf` 파일에 스트림을 정의했으나, 해당 파일이 Janus에서 로딩되지 않음
- 해결: 
  - `/opt/janus/etc/janus/janus.plugin.streaming.jcfg` 파일에 RTSP 스트림 설정을 직접 추가
  - `.conf` 파일은 삭제하거나 주석 처리하여 중복 로딩 방지
  - Janus 재시작 후 정상 인식 확인

---

### 🔧 2. 스트림 ID 중복 오류
- 오류 로그: `A stream with the provided ID already exists`
- 원인: Janus 서버가 재시작되지 않은 상태에서 같은 ID로 여러 번 요청이 들어감
- 해결:
    - Janus 서버 재시작 후 `.jcfg` 파일 내 스트림 ID 충돌이 없도록 확인
    - 클라이언트에서 ID 고정 시 서버와 충돌하지 않도록 관리 필요

---

### 🔧 3. RTSP 연결 확인이 안될 때
- 문제: RTSP 주소는 맞지만 Janus에서 영상이 수신되지 않음 
- 해결:
    - `ffmpeg -i rtsp://... -f null -` 명령어로 RTSP 스트림 직접 확인
    - 정상 수신이 되는 경우, Janus plugin이 설치된 방식이나 인증 설정 등을 재확인

---

### 🔧 4. Janus HTTP API 미작동 (JSEP 미수신)
- 문제: JSEP(offer SDP)가 오지 않아 WebRTC 연결이 진행되지 않음
- 원인: Janus 서버가 `plugin.streaming`을 로드하지 못한 상태에서 실행됨
- 해결:
    - `/opt/janus/etc/janus/janus.plugin.streaming.jcfg` 경로와 권한 재확인
    - `journalctl -u janus -f`로 실시간 로그 확인하여 플러그인 정상 로딩 확인

---

### 🔧 5. Web에서 영상이 너무 크게 출력됨
- 문제: 전체 화면 비율을 고려하지 않고 video 태그를 출력함 
- 해결:
    - `video` 태그에 `max-width: 100%; height: auto; border-radius: 8px;` 스타일 적용
    - 반응형 스타일 적용하여 다양한 해상도에 맞게 영상 크기 조정
```
video {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}
```
---

### 🔧 6. CORS 오류 (다른 PC에서 접근 시)
- 문제: React 앱이 백엔드 서버에 요청할 때 CORS 차단됨
- 해결:
    - `CorsConfig.java`에서 `setAllowedOriginPatterns(List.of("*")) `또는 내부망 IP 대역 허용
    - 예: `http://192.168.*.*:3000`, `http://*.local:3000`
    - 
---

## ✨ 최종 상태 
- ✅ Janus + RTSP + WebRTC 안정적으로 연결
- ✅ 영상 스트리밍 시작/중지 API 정상 작동
- ✅ React 기반 영상 UI 정상 표시 및 반응형 적용 완료

---

## ✉️ 작성자
#### ✉️ Email: amgkim21@gmail.com
#### 📌 GitHub: [github.com/mingstagram](https://github.com/mingstagram)
#### 📌 Tech Blog: [mingucci.tistory.com](https://mingucci.tistory.com)