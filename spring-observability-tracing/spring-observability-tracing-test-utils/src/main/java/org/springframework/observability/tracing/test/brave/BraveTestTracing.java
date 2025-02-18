/*
 * Copyright 2013-2021 the original author or authors.
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

package org.springframework.observability.tracing.test.brave;

import java.io.Closeable;

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.http.HttpTracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.sampler.Sampler;

import org.springframework.observability.tracing.CurrentTraceContext;
import org.springframework.observability.tracing.Tracer;
import org.springframework.observability.tracing.http.HttpClientHandler;
import org.springframework.observability.tracing.http.HttpRequestParser;
import org.springframework.observability.tracing.http.HttpServerHandler;
import org.springframework.observability.tracing.propagation.Propagator;
import org.springframework.observability.tracing.test.TestSpanHandler;
import org.springframework.observability.tracing.test.TestTracingAssertions;
import org.springframework.observability.tracing.test.TestTracingAware;
import org.springframework.observability.tracing.test.TestTracingAwareSupplier;
import org.springframework.observability.tracing.test.TracerAware;
import org.springframework.observability.tracing.test.brave.bridge.BraveAccessor;

/**
 * Brave version of test tracing.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BraveTestTracing implements TracerAware, TestTracingAware, TestTracingAwareSupplier, Closeable {

	brave.test.TestSpanHandler spans = new brave.test.TestSpanHandler();

	Sampler sampler = Sampler.ALWAYS_SAMPLE;

	ThreadLocalCurrentTraceContext context = ThreadLocalCurrentTraceContext.newBuilder()
			.addScopeDecorator(StrictScopeDecorator.create()).build();

	Tracing.Builder builder = tracingBuilder();

	Tracing tracing = builder.build();

	/**
	 * Sets a {@link Tracing.Builder}.
	 * @return this
	 */
	public Tracing.Builder tracingBuilder() {
		Tracing.Builder builder = Tracing.newBuilder().currentTraceContext(context).sampler(this.sampler)
				.addSpanHandler(spanHandler());
		this.builder = builder;
		return builder;
	}

	/**
	 * Sets a {@link Tracing.Builder}.
	 * @param builder tracing builder
	 * @return this
	 */
	public BraveTestTracing tracingBuilder(Tracing.Builder builder) {
		this.builder = builder;
		return this;
	}

	brave.Tracer tracer = this.tracing.tracer();

	HttpTracing httpTracing = httpTracingBuilder().build();

	/**
	 * Sets a {@link HttpTracing.Builder}.
	 * @return this
	 */
	public HttpTracing.Builder httpTracingBuilder() {
		return HttpTracing.newBuilder(this.tracing);
	}

	@Override
	public Tracer tracer() {
		return BraveAccessor.tracer(this.tracer);
	}

	@Override
	public TracerAware sampler(TraceSampler sampler) {
		this.sampler = sampler == TraceSampler.ON ? Sampler.ALWAYS_SAMPLE : Sampler.NEVER_SAMPLE;
		this.builder = tracingBuilder();
		reset();
		return this;
	}

	/**
	 * Resets the tracing components to defaults.
	 */
	public void reset() {
		this.tracing = this.builder.build();
		this.tracer = this.tracing.tracer();
		this.httpTracing = httpTracingBuilder().build();
	}

	SpanHandler spanHandler() {
		return this.spans;
	}

	@Override
	public CurrentTraceContext currentTraceContext() {
		return BraveAccessor.currentTraceContext(this.context);
	}

	@Override
	public Propagator propagator() {
		return BraveAccessor.propagator(this.tracing);
	}

	@Override
	public HttpServerHandler httpServerHandler() {
		return BraveAccessor.httpServerHandler(brave.http.HttpServerHandler.create(this.httpTracing));
	}

	@Override
	public TracerAware clientRequestParser(HttpRequestParser httpRequestParser) {
		reset();
		this.httpTracing = this.httpTracing.toBuilder()
				.clientRequestParser(BraveAccessor.httpRequestParser(httpRequestParser)).build();
		return this;
	}

	@Override
	public HttpClientHandler httpClientHandler() {
		return BraveAccessor.httpClientHandler(brave.http.HttpClientHandler.create(this.httpTracing));
	}

	@Override
	public TracerAware tracing() {
		return this;
	}

	@Override
	public TestSpanHandler handler() {
		return new BraveTestSpanHandler(this.spans);
	}

	@Override
	public TestTracingAssertions assertions() {
		return new BraveTestTracingAssertions();
	}

	@Override
	public TestTracingAware tracerTest() {
		return this;
	}

	@Override
	public void close() {
		this.spans.clear();
		this.context.clear();
		handler().clear();
		this.sampler = Sampler.ALWAYS_SAMPLE;
	}

}
