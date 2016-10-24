package com.mindlin.mdns;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Tester {
	public static void main(String...fred) throws SecurityException, IOException, InterruptedException, ExecutionException {
		MDNSListener listener = new MDNSListener();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(listener);
		Thread.sleep(500);
		listener.query("raspberrypi.local");
		listener.query("_services._dns-sd._udp.local");
		Thread.sleep(1000);
		System.exit(0);
	}
}
