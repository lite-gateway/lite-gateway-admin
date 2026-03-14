package com.litegateway.admin.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger/OpenAPI 解析工具
 */
@Slf4j
@Component
public class SwaggerParser {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 从URL解析Swagger文档
     */
    public JsonNode parseFromUrl(String url) {
        try {
            String json = restTemplate.getForObject(url, String.class);
            if (StringUtils.isBlank(json)) {
                throw new RuntimeException("Swagger文档为空");
            }
            return objectMapper.readTree(json);
        } catch (Exception e) {
            log.error("解析Swagger文档失败: {}", url, e);
            throw new RuntimeException("解析Swagger文档失败: " + e.getMessage());
        }
    }

    /**
     * 解析Swagger 2.0
     */
    public List<SwaggerApiInfo> parseSwagger2(JsonNode root) {
        List<SwaggerApiInfo> apis = new ArrayList<>();

        // 获取基础路径
        String basePath = root.path("basePath").asText("");
        JsonNode paths = root.path("paths");

        if (paths.isObject()) {
            paths.fields().forEachRemaining(entry -> {
                String path = basePath + entry.getKey();
                JsonNode methods = entry.getValue();

                methods.fields().forEachRemaining(methodEntry -> {
                    String httpMethod = methodEntry.getKey().toUpperCase();
                    JsonNode methodInfo = methodEntry.getValue();

                    SwaggerApiInfo api = new SwaggerApiInfo();
                    api.setPath(path);
                    api.setMethod(httpMethod);
                    api.setTitle(methodInfo.path("summary").asText(methodInfo.path("operationId").asText(path)));
                    api.setDescription(methodInfo.path("description").asText(""));

                    // 解析标签
                    JsonNode tags = methodInfo.path("tags");
                    if (tags.isArray() && tags.size() > 0) {
                        api.setTags(tags.get(0).asText());
                    }

                    apis.add(api);
                });
            });
        }

        return apis;
    }

    /**
     * 解析OpenAPI 3.0
     */
    public List<SwaggerApiInfo> parseOpenApi3(JsonNode root) {
        List<SwaggerApiInfo> apis = new ArrayList<>();

        // 获取服务器URL
        JsonNode servers = root.path("servers");
        String baseUrl = "";
        if (servers.isArray() && servers.size() > 0) {
            baseUrl = servers.get(0).path("url").asText("");
        }

        JsonNode paths = root.path("paths");

        if (paths.isObject()) {
            paths.fields().forEachRemaining(entry -> {
                String path = entry.getKey();
                JsonNode methods = entry.getValue();

                methods.fields().forEachRemaining(methodEntry -> {
                    String httpMethod = methodEntry.getKey().toUpperCase();
                    JsonNode methodInfo = methodEntry.getValue();

                    SwaggerApiInfo api = new SwaggerApiInfo();
                    api.setPath(path);
                    api.setMethod(httpMethod);
                    api.setTitle(methodInfo.path("summary").asText(methodInfo.path("operationId").asText(path)));
                    api.setDescription(methodInfo.path("description").asText(""));

                    // 解析标签
                    JsonNode tags = methodInfo.path("tags");
                    if (tags.isArray() && tags.size() > 0) {
                        api.setTags(tags.get(0).asText());
                    }

                    apis.add(api);
                });
            });
        }

        return apis;
    }

    /**
     * 自动判断版本并解析
     */
    public List<SwaggerApiInfo> parse(String url) {
        JsonNode root = parseFromUrl(url);

        // 判断是Swagger 2.0还是OpenAPI 3.0
        String swagger = root.path("swagger").asText();
        String openapi = root.path("openapi").asText();

        if (StringUtils.isNotBlank(swagger) && swagger.startsWith("2.")) {
            return parseSwagger2(root);
        } else if (StringUtils.isNotBlank(openapi) && openapi.startsWith("3.")) {
            return parseOpenApi3(root);
        } else {
            throw new RuntimeException("不支持的Swagger版本");
        }
    }

    /**
     * Swagger API信息
     */
    public static class SwaggerApiInfo {
        private String path;
        private String method;
        private String title;
        private String description;
        private String tags;

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
    }
}
