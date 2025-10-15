package com.api.beard_soft;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@EnableAutoConfiguration(exclude = { H2ConsoleAutoConfiguration.class })
@SpringBootTest
class BeardSoftApplicationTests {

	@Test
	void contextLoads() {
	}

}
