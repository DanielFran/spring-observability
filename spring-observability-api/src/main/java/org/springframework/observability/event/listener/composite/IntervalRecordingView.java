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

package org.springframework.observability.event.listener.composite;

import java.time.Duration;

import org.springframework.observability.event.interval.IntervalEvent;
import org.springframework.observability.event.interval.IntervalRecording;
import org.springframework.observability.event.listener.RecordingListener;
import org.springframework.observability.event.tag.Tag;

/**
 * The sole purpose of this class is being able to return the right context of the right
 * listener. All of the implemented methods are just delegating the work. except
 * {@link IntervalRecordingView#getContext()} which looks up the context from the
 * {@link CompositeContext}.
 *
 * @param <T> Context Type
 * @author Jonatan Ivanov
 */
class IntervalRecordingView<T> implements IntervalRecording<T> {

	private final RecordingListener<?> listener;

	private final IntervalRecording<CompositeContext> delegate;

	IntervalRecordingView(RecordingListener<?> listener, IntervalRecording<CompositeContext> delegate) {
		this.listener = listener;
		this.delegate = delegate;
	}

	@Override
	public IntervalEvent getEvent() {
		return this.delegate.getEvent();
	}

	@Override
	public String getHighCardinalityName() {
		return this.delegate.getHighCardinalityName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IntervalRecording<T> highCardinalityName(String highCardinalityName) {
		return (IntervalRecording<T>) this.delegate.highCardinalityName(highCardinalityName);
	}

	@Override
	public Iterable<Tag> getTags() {
		return this.delegate.getTags();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IntervalRecording<T> tag(Tag tag) {
		return (IntervalRecording<T>) this.delegate.tag(tag);
	}

	@Override
	public Duration getDuration() {
		return this.delegate.getDuration();
	}

	@Override
	public long getStartNanos() {
		return this.delegate.getStartNanos();
	}

	@Override
	public long getStopNanos() {
		return this.delegate.getStopNanos();
	}

	@Override
	public long getStartWallTime() {
		return this.delegate.getStartWallTime();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IntervalRecording<T> start() {
		return (IntervalRecording<T>) this.delegate.start();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IntervalRecording<T> start(long wallTime, long monotonicTime) {
		return (IntervalRecording<T>) this.delegate.start(wallTime, monotonicTime);
	}

	@Override
	public void stop(long monotonicTime) {
		this.delegate.stop(monotonicTime);
	}

	@Override
	public void stop() {
		this.delegate.stop();
	}

	@Override
	public Throwable getError() {
		return this.delegate.getError();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IntervalRecording<T> error(Throwable error) {
		return (IntervalRecording<T>) this.delegate.error(error);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getContext() {
		return (T) this.delegate.getContext().byListener(listener);
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

}
