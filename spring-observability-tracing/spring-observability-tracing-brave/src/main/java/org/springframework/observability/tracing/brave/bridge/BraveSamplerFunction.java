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

import brave.sampler.SamplerFunctions;

import org.springframework.observability.core.http.HttpRequest;
import org.springframework.observability.tracing.SamplerFunction;

/**
 * Brave implementation of a {@link SamplerFunction}.
 *
 * @param <T> type of the input, for example a request or method
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public final class BraveSamplerFunction<T> implements SamplerFunction<T> {

	final brave.sampler.SamplerFunction<T> samplerFunction;

	/**
	 * @param samplerFunction Brave delegate
	 */
	public BraveSamplerFunction(brave.sampler.SamplerFunction<T> samplerFunction) {
		this.samplerFunction = samplerFunction;
	}

	static <T, V> brave.sampler.SamplerFunction<V> toBrave(SamplerFunction<T> samplerFunction, Class<T> sleuthInput,
			Class<V> braveInput) {
		if (sleuthInput.equals(HttpRequest.class) && braveInput.equals(brave.http.HttpRequest.class)) {
			return arg -> samplerFunction.trySample((T) BraveHttpRequest.fromBrave((brave.http.HttpRequest) arg));
		}
		return SamplerFunctions.deferDecision();
	}

	/**
	 * Converts from Spring Observability to Brave.
	 * @param samplerFunction Spring Observability delegate
	 * @return converted version
	 */
	public static brave.sampler.SamplerFunction<brave.http.HttpRequest> toHttpBrave(
			SamplerFunction<HttpRequest> samplerFunction) {
		return arg -> samplerFunction.trySample(BraveHttpRequest.fromBrave(arg));
	}

	@Override
	public Boolean trySample(T arg) {
		return this.samplerFunction.trySample(arg);
	}

}
