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

import org.springframework.observability.core.http.HttpResponse;
import org.springframework.observability.tracing.SpanCustomizer;
import org.springframework.observability.tracing.TraceContext;
import org.springframework.observability.tracing.http.HttpResponseParser;

/**
 * Brave implementation of a {@link HttpResponseParser}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BraveHttpResponseParser implements HttpResponseParser {

	final brave.http.HttpResponseParser delegate;

	/**
	 * @param delegate Brave delegate
	 */
	public BraveHttpResponseParser(brave.http.HttpResponseParser delegate) {
		this.delegate = delegate;
	}

	/**
	 * @param parser Sleuth's API parser
	 * @return Brave's parser
	 */
	public static brave.http.HttpResponseParser toBrave(HttpResponseParser parser) {
		if (parser instanceof BraveHttpResponseParser) {
			return ((BraveHttpResponseParser) parser).delegate;
		}
		return (response, context, span) -> parser.parse(BraveHttpResponse.fromBrave(response),
				BraveTraceContext.fromBrave(context), BraveSpanCustomizer.fromBrave(span));
	}

	@Override
	public void parse(HttpResponse response, TraceContext context, SpanCustomizer span) {
		this.delegate.parse(BraveHttpResponse.toBrave(response), BraveTraceContext.toBrave(context),
				BraveSpanCustomizer.toBrave(span));
	}

}
