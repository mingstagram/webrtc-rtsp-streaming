package com.demo.video_streaming.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class JanusService {

    private final WebClient webClient;

    @Value("${janus.server-url}") // application.yml에서 Janus 서버 URL 가져오기
    private String janusServerUrl;

    /**
     * ✅ Janus 세션 생성
     */
    public Mono<String> createSession() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("janus", "create");
        requestBody.put("transaction", "session-create");

        return webClient.post()
                .uri(janusServerUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    log.info("✅ Janus 세션 응답: {}", jsonResponse.toString(2));

                    if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("id")) {
                        return Mono.just(String.valueOf(jsonResponse.getJSONObject("data").getLong("id")));
                    } else {
                        return Mono.error(new RuntimeException("❌ 세션 생성 실패: 응답 데이터 없음"));
                    }
                });
    }

    /**
     * ✅ Streaming 플러그인 Attach
     */
    public Mono<String> attachPlugin(String sessionId) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("janus", "attach");
        requestBody.put("plugin", "janus.plugin.streaming");
        requestBody.put("transaction", "attach-plugin");

        return webClient.post()
                .uri(janusServerUrl + "/" + sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    log.info("✅ Janus 플러그인 Attach 응답: {}", jsonResponse.toString(2));

                    if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("id")) {
                        return Mono.just(String.valueOf(jsonResponse.getJSONObject("data").getLong("id")));
                    } else {
                        return Mono.error(new RuntimeException("❌ 플러그인 Attach 실패: 응답 데이터 없음"));
                    }
                });
    }

    /**
     * ✅ RTSP → WebRTC 스트리밍 마운트 생성
     */
    public Mono<String> createStreamingMountpoint() {
        return createSession()
                .flatMap(sessionId -> attachPlugin(sessionId)
                        .flatMap(handleId -> {
                            JSONObject requestBody = new JSONObject();
                            requestBody.put("janus", "message");
                            requestBody.put("transaction", "create-stream");
                            requestBody.put("session_id", sessionId);
                            requestBody.put("handle_id", handleId);

                            JSONObject body = new JSONObject();
                            body.put("request", "create");
                            body.put("id", 1);
                            body.put("type", "rtsp");
                            body.put("rtsp", "rtsp://admin:admin@192.168.10.10:554/media/1/1/Profile1");
                            body.put("rtsp_transport", "tcp");
                            body.put("video", true);
                            body.put("audio", false);
                            requestBody.put("body", body);

                            return webClient.post()
                                    .uri(janusServerUrl + "/" + sessionId + "/" + handleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(requestBody.toString())
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .flatMap(response -> {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        log.info("✅ Janus 스트리밍 응답: {}", jsonResponse.toString(2));

                                        if (jsonResponse.has("plugindata")) {
                                            return Mono.just(jsonResponse.toString());
                                        } else {
                                            return Mono.error(new RuntimeException("❌ 스트리밍 생성 실패: 응답 데이터 없음"));
                                        }
                                    });
                        })
                );
    }

    public Mono<Void> stopStreaming(String sessionId, String handleId) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("janus", "hangup");
        requestBody.put("transaction", "stop-stream");

        return webClient.post()
                .uri(janusServerUrl + "/" + sessionId + "/" + handleId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("✅ 스트리밍 중지 응답: {}", response))
                .then(); // void 반환
    }

}
