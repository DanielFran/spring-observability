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

package org.springframework.observability.tracing.brave.bridge;

import org.springframework.core.log.LogAccessor;
import org.springframework.observability.core.http.HttpClientRequest;
import org.springframework.observability.core.http.HttpClientResponse;
import org.springframework.observability.tracing.Span;
import org.springframework.observability.tracing.TraceContext;
import org.springframework.observability.tracing.http.HttpClientHandler;

/**
 * Brave implementation of a {@link HttpClientHandler}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BraveHttpClientHandler implements HttpClientHandler {

	private static final LogAccessor log = new LogAccessor(BraveHttpClientHandler.class);

	final brave.http.HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> delegate;

	/**
	 * @param delegate Brave delegate
	 */
	public BraveHttpClientHandler(
			brave.http.HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> delegate) {
		this.delegate = delegate;
	}

	@Override
	public Span handleSend(HttpClientRequest request) {
		return BraveSpan.fromBrave(this.delegate.handleSend(BraveHttpClientRequest.toBrave(request)));
	}

	@Override
	public Span handleSend(HttpClientRequest request, TraceContext parent) {
		brave.Span span = this.delegate.handleSendWithParent(BraveHttpClientRequest.toBrave(request),
				BraveTraceContext.toBrave(parent));
		if (!span.isNoop()) {
			span.remoteIpAndPort(request.remoteIp(), request.remotePort());
		}
		return BraveSpan.fromBrave(span);
	}

	@Override
	public void handleReceive(HttpClientResponse response, Span span) {
		if (response == null) {
			log.debug(() -> "Response is null, will not handle receiving of span [" + span + "]");
			return;
		}
		this.delegate.handleReceive(BraveHttpClientResponse.toBrave(response), BraveSpan.toBrave(span));
	}

}
