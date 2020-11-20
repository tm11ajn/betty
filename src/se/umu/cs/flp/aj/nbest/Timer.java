/*
 * Copyright 2019 Frank Drewes for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of BestTrees.
 *
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created in 2019 by drewes.
 */

package se.umu.cs.flp.aj.nbest;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

/* A class for measuring elapsed time in milliseconds in the current thread. */
public class Timer {
	
	 ThreadMXBean threadMxBean;
	 long threadId;
	 long time;
	
	public Timer() {
		threadMxBean = ManagementFactory.getThreadMXBean();
		if (!threadMxBean.isThreadCpuTimeSupported()) {
			throw new IllegalStateException("CPU time not supported on this platform");
		}
        threadMxBean.setThreadCpuTimeEnabled(true);
		threadId = Thread.currentThread().getId();
	}
	
	/* Measure time elapsed from now on. */
	public void start() {
		time = threadMxBean.getThreadCpuTime(threadId);
	}
	
	/* How many milliseconds have elapsed until now? */
    public long elapsed() {
		return TimeUnit.NANOSECONDS.toMillis(threadMxBean.getThreadCpuTime(threadId) - time);
	}
	
}
