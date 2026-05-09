package com.yash.url_shortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
		"spring.data.redis.repositories.enabled=false",
		"gemini.api.key=test-key"
})
class UrlShortenerApplicationTests {

	@Test
	void contextLoads() {
		// Test passes if application context loads
		System.out.println("✅ Application context loaded successfully!");
	}
}