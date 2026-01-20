package com.slackcrawler.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slackcrawler.api.dto.SlackDataRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SlackDataService {

    @Value("${slack.data.save.path:data}")
    private String savePath;

    private final ObjectMapper objectMapper;

    public SlackDataService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.writerWithDefaultPrettyPrinter();
    }

    public String saveSlackData(SlackDataRequest data) throws IOException {
        // 저장 경로를 프로젝트 루트 기준으로 설정
        Path basePath = Paths.get(System.getProperty("user.dir"));
        Path dataPath;
        
        if (savePath.startsWith("/")) {
            // 절대 경로인 경우 그대로 사용
            dataPath = Paths.get(savePath);
        } else {
            // 상대 경로인 경우: slack-data-api 디렉토리에서 실행되면 상위 디렉토리로 이동
            if (basePath.getFileName().toString().equals("slack-data-api")) {
                dataPath = basePath.getParent().resolve(savePath);
            } else {
                // 프로젝트 루트에서 실행되는 경우
                dataPath = basePath.resolve(savePath);
            }
        }
        
        // 저장 디렉토리 생성
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
        }

        // 파일명 생성 (타임스탬프 포함)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        String fileName = String.format("slack-data-%s.json", timestamp);
        
        // 파일 경로 생성
        Path filePath = dataPath.resolve(fileName);

        // JSON 파일로 저장
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), data);

        return fileName;
    }

    public String getLastTimestamp(String channelId) throws IOException {
        Path timestampsFile = getTimestampsFilePath();
        
        if (!Files.exists(timestampsFile)) {
            return null;
        }
        
        try {
            String content = Files.readString(timestampsFile);
            if (content == null || content.trim().isEmpty()) {
                return null;
            }
            java.util.Map<String, String> timestamps = objectMapper.readValue(content, 
                objectMapper.getTypeFactory().constructMapType(java.util.Map.class, String.class, String.class));
            return timestamps.get(channelId);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveLastTimestamp(String channelId, String timestamp) throws IOException {
        Path timestampsFile = getTimestampsFilePath();
        Path dataPath = timestampsFile.getParent();
        
        // 디렉토리 생성
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
        }
        
        // 기존 데이터 읽기
        java.util.Map<String, String> timestamps = new java.util.HashMap<>();
        if (Files.exists(timestampsFile)) {
            try {
                String content = Files.readString(timestampsFile);
                if (content != null && !content.trim().isEmpty()) {
                    timestamps = objectMapper.readValue(content, 
                        objectMapper.getTypeFactory().constructMapType(java.util.Map.class, String.class, String.class));
                }
            } catch (Exception e) {
                // 파일이 손상된 경우 빈 맵으로 시작
                timestamps = new java.util.HashMap<>();
            }
        }
        
        // timestamp 업데이트
        timestamps.put(channelId, timestamp);
        
        // 파일에 저장
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(timestampsFile.toFile(), timestamps);
    }

    private Path getTimestampsFilePath() {
        Path basePath = Paths.get(System.getProperty("user.dir"));
        Path dataPath;
        
        if (savePath.startsWith("/")) {
            dataPath = Paths.get(savePath);
        } else {
            if (basePath.getFileName().toString().equals("slack-data-api")) {
                dataPath = basePath.getParent().resolve(savePath);
            } else {
                dataPath = basePath.resolve(savePath);
            }
        }
        
        return dataPath.resolve("slack-timestamps.json");
    }
}

