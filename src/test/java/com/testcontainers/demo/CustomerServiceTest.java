package com.testcontainers.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class CustomerServiceTest {

  // PostgreSQL コンテナの定義。イメージは "postgres:16-alpine" を使用。
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

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
      postgres.getPassword()
    );
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
}