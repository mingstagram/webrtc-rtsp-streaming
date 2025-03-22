package com.demo.video_streaming.controller;

import com.demo.video_streaming.service.JanusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/janus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 🔥 React와의 CORS 문제 해결
public class JanusController {

    private final JanusService janusService;

    /**
     * ✅ WebRTC 세션 생성 요청
     */
    @PostMapping("/session")
    public Mono<ResponseEntity<String>> createSession() {
        log.info("🆕 Janus WebRTC 세션 생성 요청됨...");
        return janusService.createSession()
                .flatMap(sessionId -> {
                    log.info("✅ 세션 생성 성공! ID: {}", sessionId);
                    return Mono.just(ResponseEntity.ok("{\"id\":" + sessionId + "}"));
                })
                .onErrorResume(e -> {
                    log.error("❌ 세션 생성 실패", e);
                    return Mono.just(ResponseEntity.internalServerError().body("❌ 세션 생성 실패"));
                });
    }

    /**
     * ✅ 플러그인 핸들러 연결 요청
     */
    @PostMapping("/attach")
    public Mono<ResponseEntity<String>> attachPlugin(@RequestBody Map<String, String> request) {
        String sessionId = request.get("session_id");
        log.info("🆕 Janus 플러그인 Attach 요청: Session ID = {}", sessionId);
        return janusService.attachPlugin(sessionId)
                .flatMap(handleId -> {
                    log.info("✅ 플러그인 핸들러 연결 성공! Handle ID: {}", handleId);
                    return Mono.just(ResponseEntity.ok("{\"handle_id\":" + handleId + "}"));
                })
                .onErrorResume(e -> {
                    log.error("❌ 플러그인 핸들러 연결 실패", e);
                    return Mono.just(ResponseEntity.internalServerError().body("❌ 플러그인 핸들러 연결 실패"));
                });
    }

    /**
     * ✅ RTSP → WebRTC 스트리밍 시작 요청
     */
    @PostMapping("/stream")
    public Mono<ResponseEntity<String>> startStreaming() {
        log.info("▶️ RTSP → WebRTC 스트리밍 시작 요청됨...");
        return janusService.createStreamingMountpoint()
                .flatMap(response -> {
                    log.info("✅ 스트리밍 마운트 성공: {}", response);
                    return Mono.just(ResponseEntity.ok(response));
                })
                .onErrorResume(e -> {
                    log.error("❌ 스트리밍 생성 실패", e);
                    return Mono.just(ResponseEntity.internalServerError().body("❌ 스트리밍 생성 실패"));
                });
    }

    /**
     * 🛑 스트리밍 중지 요청
     */
    @PostMapping("/stop")
    public Mono<ResponseEntity<String>> stopStreaming(@RequestBody Map<String, String> request) {
        String sessionId = request.get("session_id");
        String handleId = request.get("handle_id");

        log.info("🛑 스트리밍 중지 요청: sessionId={}, handleId={}", sessionId, handleId);

        return janusService.stopStreaming(sessionId, handleId)
                .thenReturn(ResponseEntity.ok("🛑 스트리밍 중지 성공"))
                .onErrorResume(e -> {
                    log.error("❌ 스트리밍 중지 실패", e);
                    return Mono.just(ResponseEntity.internalServerError().body("❌ 스트리밍 중지 실패"));
                });
    }
}
