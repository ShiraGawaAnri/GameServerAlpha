package com.nekonade.common.concurrent;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;

public class GameEventExecutorGroup {

	private final EventExecutor[] executors;
	
	public GameEventExecutorGroup(int threads, String name) {
		if(threads <= 0) {
			threads = Runtime.getRuntime().availableProcessors();
		}
		executors = new DefaultEventExecutor[threads];
		for(int i = 0;i < threads;i++) {
			executors[i] = new DefaultEventExecutor(new DefaultThreadFactory(name));
		}
	}
	
	public void Shutdown() {
		for(EventExecutor executor : executors) {
			executor.shutdownGracefully();
		}
	}
	public boolean isAllShudownComplete() {
		for(EventExecutor executor : executors) {
			if(!executor.isTerminated()) {
				return false;
			}
		}
		return true;
	}
	
	public EventExecutor select(Object key) {
		int value = key.hashCode();
		if (isPowerOfTwo(this.executors.length)) {
            return executors[value & executors.length - 1];
        } else {
            return executors[Math.abs(value % executors.length)];
        }
	}
	
    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }
}
