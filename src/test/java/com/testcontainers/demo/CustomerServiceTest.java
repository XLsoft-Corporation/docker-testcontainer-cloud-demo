package com.testcontainers.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerServiceTest {

  // 実行環境判別用の定数
  public static final String DOCKER_CLOUD_VERSION_LABEL = "cloud.docker.run.version";
  public static final String TESTCONTAINERS_DESKTOP_APP_NAME = "Testcontainers Desktop";
  public static final String TESTCONTAINERS_CLOUD_VERSION_NAME = "testcontainerscloud";

  // PostgreSQL コンテナの定義。イメージは "postgres:16-alpine" を使用。
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");

  CustomerService customerService;

  // テストクラス全体の前に一度だけ実行されるセットアップメソッド。PostgreSQL コンテナを起動。
  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  // テストクラス全体の後に一度だけ実行されるクリーンアップメソッド。PostgreSQL コンテナを停止。
  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  // 各テストケースの前に実行されるセットアップメソッド。DB への接続を初期化。
  @BeforeEach
  void setUp() {
    DBConnectionProvider connectionProvider = new DBConnectionProvider(
        postgres.getJdbcUrl(),
        postgres.getUsername(),
        postgres.getPassword());
    customerService = new CustomerService(connectionProvider);
  }

  // 実際のテストケース
  @Test
  void shouldGetCustomers() {
    customerService.createCustomer(new Customer(1L, "George"));
    customerService.createCustomer(new Customer(2L, "John"));

    List<Customer> customers = customerService.getAllCustomers();
    assertEquals(2, customers.size());
  }

  // Docker クライアントの情報をログに出力するユーティリティメソッド
  @Test
  public void testcontainersCloudDockerEngine() {
    DockerClient client = DockerClientFactory.instance().client();
    Info dockerInfo = client.infoCmd().exec();

    String serverVersion = dockerInfo.getServerVersion();
    String[] labels = dockerInfo.getLabels();

    List<String> info = Streams.concat(
        Stream.of(String.format("server.version=%s", serverVersion)),
        Arrays.stream(labels == null ? new String[] {} : labels)).collect(Collectors.toList());

    assertThat(info)
        .as("Docker Client is configured via the Testcontainers desktop app")
        .anySatisfy(it -> assertThat(it).containsAnyOf(
            TESTCONTAINERS_DESKTOP_APP_NAME,
            TESTCONTAINERS_CLOUD_VERSION_NAME,
            DOCKER_CLOUD_VERSION_LABEL,
            "server.version"));

    logRuntimeDetails(serverVersion != null ? serverVersion : "", dockerInfo);
  }

  private static void logRuntimeDetails(String serverVersion, Info dockerInfo) {
    String runtimeName = "Testcontainers Cloud";
    boolean hasCloudLabel = Stream.of(
        dockerInfo.getLabels() != null
            ? dockerInfo.getLabels()
            : new String[] {})
        .anyMatch(label -> label.contains(DOCKER_CLOUD_VERSION_LABEL));
    if (!serverVersion.contains(TESTCONTAINERS_CLOUD_VERSION_NAME) && !hasCloudLabel) {
      runtimeName = dockerInfo.getOperatingSystem();
    }
    if (serverVersion.contains(TESTCONTAINERS_DESKTOP_APP_NAME)) {
      runtimeName += " via Testcontainers Desktop";
    }
    System.out.println("\n\nTCCRUNTIME: " + runtimeName + "\n\n");
  }
}