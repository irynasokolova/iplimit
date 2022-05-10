package com.sokolova.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = { com.sokolova.test.IpLimitCache.class, IpLimitFilter.class, EmptyController.class })
@TestPropertySource("classpath:test.properties")
class TestApplicationTests {
	private final static String IP_1 = "192.168.0.1";
	private final static String IP_2 = "192.168.0.2";
	private final static String IP_3 = "192.168.0.3";
	@Value("${fixed.rate}")
	private long sleepTime;

	@Value("${number}")
	private int n;

	@Value("${deep}")
	private long d;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGet1() throws Exception {
		testGet(IP_1);
	}

	@Test
	public void testGet2() throws Exception {
		testGet(IP_2);
	}

	@Test
	public void testGet3() throws Exception {
		testGet(IP_3);
	}

	public void testGet(String ip) throws Exception {
		throwRequests(ip);
	}

	private static RequestPostProcessor remoteAddr(final String remoteAddr) {
		return new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setRemoteAddr(remoteAddr);
				return request;
			}
		};
	}

	private void throwRequests(String ip) throws Exception {
		for (int i = 0; i < n; i++) {
			Thread.sleep((long) (Math.random() * 100));
			mockMvc.perform(MockMvcRequestBuilders.get("/").with(remoteAddr(ip)).accept(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(status().isOk());

		}
		for (int i = 0; i < n; i++) {
			Thread.sleep((long) (Math.random() * 100));
			mockMvc.perform(MockMvcRequestBuilders.get("/").with(remoteAddr(ip)).accept(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(status().isBadGateway());
		}

		Thread.sleep(sleepTime / 2 + 1);
		for (int i = 0; i < n; i++) {
			Thread.sleep((long) (Math.random() * 100));
			mockMvc.perform(MockMvcRequestBuilders.get("/").with(remoteAddr(ip)).accept(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(status().isBadGateway());
		}
		Thread.sleep(sleepTime / 2 + 1);

	}
}
