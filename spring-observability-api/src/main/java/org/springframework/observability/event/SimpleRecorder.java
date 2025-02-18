/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.observability.event;

import org.springframework.observability.event.instant.InstantEvent;
import org.springframework.observability.event.instant.InstantRecording;
import org.springframework.observability.event.instant.NoOpInstantRecording;
import org.springframework.observability.event.instant.SimpleInstantRecording;
import org.springframework.observability.event.interval.IntervalEvent;
import org.springframework.observability.event.interval.IntervalRecording;
import org.springframework.observability.event.interval.NoOpIntervalRecording;
import org.springframework.observability.event.interval.SimpleIntervalRecording;
import org.springframework.observability.event.listener.RecordingListener;
import org.springframework.observability.time.Clock;

/**
 * @param <T> Context Type
 * @author Jonatan Ivanov
 * @since 1.0.0
 */
public class SimpleRecorder<T> implements Recorder<T> {

	private final RecordingListener<T> listener;

	private final Clock clock;

	private volatile boolean enabled;

	/**
	 * @param listener The listener that needs to be notified about the recordings.
	 * @param clock The clock to be used.
	 */
	public SimpleRecorder(RecordingListener<T> listener, Clock clock) {
		this.listener = listener;
		this.clock = clock;
		this.enabled = true;
	}

	@Override
	public IntervalRecording<T> recordingFor(IntervalEvent event) {
		return enabled ? new SimpleIntervalRecording<>(event, listener, clock) : new NoOpIntervalRecording<>();
	}

	@Override
	public InstantRecording recordingFor(InstantEvent event) {
		return enabled ? new SimpleInstantRecording(event, listener, clock) : new NoOpInstantRecording();
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
