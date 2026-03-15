package com.litegateway.admin.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litegateway.admin.cache.InstanceCacheManager;
import com.litegateway.admin.query.InstanceQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nacos 服务客户端
 * 封装 Nacos SDK 和 HTTP API 调用，提供统一的服务查询接口
 * 仅在 Nacos 启用时加载
 */
@Slf4j
@Component
@ConditionalOnBean(NamingService.class)
public class NacosServiceClient {

    @Autowired
    private NamingService namingService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InstanceCacheManager cacheManager;

    @Value("${spring.cloud.nacos.discovery.server-addr:localhost:8848}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}")
    private String group;

    private final String clusterName = "DEFAULT";

    /**
     * 获取所有服务列表
     * 优先从缓存获取，缓存未命中则从 Nacos 查询
     */
    public List<String> getAllServices() {
        // 尝试从缓存获取
        List<String> cachedServices = cacheManager.getServices("all_services");
        if (cachedServices != null && !cachedServices.isEmpty()) {
            log.debug("Returning cached services list, size: {}", cachedServices.size());
            return cachedServices;
        }

        // 缓存未命中，从 Nacos 查询
        try {
            ListView<String> servicesView = namingService.getServicesOfServer(1, Integer.MAX_VALUE);
            List<String> services = servicesView.getData();

            // 更新缓存
            cacheManager.putServices("all_services", services);
            log.info("Fetched {} services from Nacos", services.size());
            return services;
        } catch (NacosException e) {
            log.error("Failed to get services from Nacos", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取服务的所有实例
     * 优先从缓存获取，缓存未命中则从 Nacos 查询
     */
    public List<Instance> getAllInstances(String serviceName) {
        // 尝试从缓存获取
        List<Instance> cachedInstances = cacheManager.getInstances(serviceName);
        if (cachedInstances != null) {
            log.debug("Returning cached instances for service: {}, size: {}", serviceName, cachedInstances.size());
            return cachedInstances;
        }

        // 缓存未命中，从 Nacos 查询
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName, group);

            // 更新缓存
            cacheManager.putInstances(serviceName, instances);
            log.info("Fetched {} instances from Nacos for service: {}", instances.size(), serviceName);
            return instances;
        } catch (NacosException e) {
            log.error("Failed to get instances from Nacos for service: {}", serviceName, e);
            return Collections.emptyList();
        }
    }

    /**
     * 分页获取服务实例
     * 使用 Nacos HTTP API 进行分页查询
     */
    public PageResult<Instance> getInstancesPage(InstanceQuery query) {
        String serviceName = query.getServiceName();
        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;

        // 构建请求 URL
        String serviceNameWithGroup = group + "@@" + serviceName;
        String url = String.format("http://%s/nacos/v1/ns/catalog/instances?serviceName=%s&clusterName=%s&namespaceId=%s&pageSize=%d&pageNo=%d",
                serverAddr, serviceNameWithGroup, clusterName, group, pageSize, pageNum);

        log.debug("Query Nacos instances page: {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isEmpty()) {
                return PageResult.empty(pageNum, pageSize);
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode listNode = root.get("list");
            JsonNode countNode = root.get("count");

            int total = countNode != null ? countNode.asInt() : 0;
            List<Instance> instances = new ArrayList<>();

            if (listNode != null && listNode.isArray()) {
                for (JsonNode node : listNode) {
                    Instance instance = parseInstanceFromJson(node);
                    if (instance != null) {
                        instances.add(instance);
                    }
                }
            }

            log.info("Fetched {} instances from Nacos page for service: {} (page: {}, size: {})",
                    instances.size(), serviceName, pageNum, pageSize);

            return new PageResult<>(instances, total, pageNum, pageSize);

        } catch (Exception e) {
            log.error("Failed to get instances page from Nacos for service: {}", serviceName, e);
            return PageResult.empty(pageNum, pageSize);
        }
    }

    /**
     * 获取服务实例总数
     */
    public int getInstanceCount(String serviceName) {
        String serviceNameWithGroup = group + "@@" + serviceName;
        String url = String.format("http://%s/nacos/v1/ns/catalog/instances?serviceName=%s&clusterName=%s&namespaceId=%s&pageSize=1&pageNo=1",
                serverAddr, serviceNameWithGroup, clusterName, group);

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isEmpty()) {
                return 0;
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode countNode = root.get("count");
            return countNode != null ? countNode.asInt() : 0;
        } catch (Exception e) {
            log.error("Failed to get instance count from Nacos for service: {}", serviceName, e);
            return 0;
        }
    }

    /**
     * 从 JSON 节点解析 Instance 对象
     */
    private Instance parseInstanceFromJson(JsonNode node) {
        try {
            Instance instance = new Instance();
            instance.setInstanceId(getTextValue(node, "instanceId"));
            instance.setIp(getTextValue(node, "ip"));
            instance.setPort(getIntValue(node, "port", 0));
            instance.setWeight(getDoubleValue(node, "weight", 1.0));
            instance.setHealthy(getBooleanValue(node, "healthy", true));
            instance.setEnabled(getBooleanValue(node, "enabled", true));
            instance.setEphemeral(getBooleanValue(node, "ephemeral", true));
            instance.setClusterName(getTextValue(node, "clusterName"));

            // 解析元数据
            JsonNode metadataNode = node.get("metadata");
            if (metadataNode != null && metadataNode.isObject()) {
                // 元数据处理
            }

            return instance;
        } catch (Exception e) {
            log.error("Failed to parse instance from JSON: {}", node, e);
            return null;
        }
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asText() : null;
    }

    private int getIntValue(JsonNode node, String fieldName, int defaultValue) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asInt(defaultValue) : defaultValue;
    }

    private double getDoubleValue(JsonNode node, String fieldName, double defaultValue) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asDouble(defaultValue) : defaultValue;
    }

    private boolean getBooleanValue(JsonNode node, String fieldName, boolean defaultValue) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asBoolean(defaultValue) : defaultValue;
    }

    /**
     * 分页结果封装类
     */
    public record PageResult<T>(List<T> list, int total, int pageNum, int pageSize) {
        public static <T> PageResult<T> empty(int pageNum, int pageSize) {
            return new PageResult<>(Collections.emptyList(), 0, pageNum, pageSize);
        }

        public int getTotalPages() {
            return pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        }

        public boolean hasNext() {
            return pageNum < getTotalPages();
        }

        public boolean hasPrevious() {
            return pageNum > 1;
        }
    }
}
