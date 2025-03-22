# 🚀 WebRTC RTSP 스트리밍 프로젝트

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4+-green?style=flat)
![WebFlux](https://img.shields.io/badge/WebFlux-Reactive-blue?style=flat) 

## 📌 프로젝트 개요
이 프로젝트는 **RTSP 스트리밍을 WebRTC로 변환**하여 **웹에서 실시간 CCTV 영상을 송출**하는 시스템입니다.
**Janus WebRTC Gateway**를 백엔드(Spring WebFlux)에서 연동하고, 프론트엔드는 **React**를 통해 실시간 영상 UI를 구현했습니다.

✅ **주요 목표:**
- RTSP 기반 CCTV 영상 → WebRTC 변환 및 브라우저 출력
- React + WebRTC로 직관적인 영상 플레이어 UI 제공
- Spring WebFlux 기반의 비동기 Janus API 컨트롤러 구성
- WebClient 활용한 스트리밍 세션 제어
- Janus HTTP API를 통한 세션 생성, 핸들 연결, 스트리밍 시작/중지

---

## 📌 기술 스택
- **Frontend**: React, HTML5 Video, WebRTC
- **Backend**: Spring Boot (WebFlux), WebClient, Lombok, SLF4J
- **Media Gateway**: Janus WebRTC Gateway
- **Streaming Protocol**: RTSP (H.264)
- **Infra**: Ubuntu, systemd, Docker (optional)
- **Build Tool**: Maven

---

## 📌 주요 기능
### ✅ 1. 실시간 RTSP → WebRTC 변환 스트리밍
- Janus Streaming Plugin과 RTSP CCTV 연동
- React 기반 클라이언트에서 실시간 영상 시청 가능

### ✅ 2. 스트리밍 시작/중지 제어 API
- `/janus/session`: Janus 세션 생성
- `/janus/attach`: Streaming 플러그인 핸들러 연결
- `/janus/stream`: RTSP 마운트 생성 및 스트리밍 시작
- `/janus/stop`: 스트리밍 중지 요청

### ✅ 3. WebRTC 클라이언트 구현
- JSEP offer 수신 및 answer 생성
- RTCPeerConnection 연결로 WebRTC 세션 구성
- 영상 크기 자동 조절 및 상태 메시지 표시

### ✅ 4. 유연한 CORS 정책
내부망 또는 로컬 개발 환경에서 자유로운 CORS 접근 허용

---

## 📁 프로젝트 문서
- 📄 [트러블슈팅 문서](docs/trouble-shooting.md)

---

## 📌 Contact
#### ✉️ Email: amgkim21@gmail.com
#### 📌 GitHub: [github.com/mingstagram](https://github.com/mingstagram)
#### 📌 Tech Blog: [mingucci.tistory.com](https://mingucci.tistory.com)