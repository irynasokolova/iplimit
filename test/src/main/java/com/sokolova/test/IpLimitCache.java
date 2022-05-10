package com.sokolova.test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class IpLimitCache implements InitializingBean {
	@Value("${number}")
	private Integer n;
	@Value("${deepness}")
	private Integer d;

	private LoadingCache<String, List<LocalDateTime>> cache;

	public boolean put(String ip) throws ExecutionException {
		List<LocalDateTime> enters = cache.get(ip);
		LocalDateTime time = LocalDateTime.now();
		enters = filterEnters(enters, time);
		if (enters.size() < n) {
			enters.add(time);
			cache.put(ip, enters);
			System.out.println("put ip = " + ip + " dates: " + enters);
			return true;
		}
		System.out.println("can't  put ip = " + ip + " because of cache  size = " + enters.size() + " > =  " + n);
		return false;

	}

	private List<LocalDateTime> filterEnters(List<LocalDateTime> enters, LocalDateTime time) {

		return enters.stream().filter(t -> ChronoUnit.MINUTES.between(t, time) < d).collect(Collectors.toList());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		CacheLoader<String, List<LocalDateTime>> loader;
		loader = new CacheLoader<String, List<LocalDateTime>>() {
			@Override
			public final List<LocalDateTime> load(final String key) {
				return new ArrayList<LocalDateTime>(1);
			}
		};
		this.cache = CacheBuilder.newBuilder().expireAfterAccess(d, TimeUnit.MINUTES).build(loader);

	}

}
