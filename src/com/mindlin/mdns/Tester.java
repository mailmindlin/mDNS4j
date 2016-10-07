package com.mindlin.mdns;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tester {
	public static void main(String...fred) throws SecurityException, IOException, InterruptedException, ExecutionException {
		MDNSListener listener = new MDNSListener();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(listener).get();
	}
}
