/*
 * Copyright 2023-2023 Maxim Sambulat.
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

package io.github.airflux.functional.kotest

import io.github.airflux.functional.Try
import io.github.airflux.functional.isError
import io.github.airflux.functional.isSuccess
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <reified T> Try<T>.shouldBeSuccess(): Try.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is Try.Success<T>)
    }

    if (this.isError()) {
        errorCollector.collectOrThrow(
            failure(
                expected = Try.Success::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Try.Success, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as Try.Success<T>
}

public inline infix fun <reified T> Try<T>.shouldBeSuccess(expected: T) {
    this.shouldBeSuccess().result shouldBe expected
}

public inline infix fun <reified T> Try<T>.shouldBeSuccess(block: (T) -> Unit) {
    block(this.shouldBeSuccess().result)
}

@OptIn(ExperimentalContracts::class)
public fun Try<*>.shouldBeFailure(): Try.Failure {
    contract {
        returns() implies (this@shouldBeFailure is Try.Failure)
    }

    if (this.isSuccess()) {
        errorCollector.collectOrThrow(
            failure(
                expected = Try.Failure::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Try.Failure, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as Try.Failure
}

public infix fun Try<*>.shouldBeFailure(expected: Throwable) {
    this.shouldBeFailure().exception shouldBe expected
}

public infix fun Try<*>.shouldBeFailure(block: (Throwable) -> Unit) {
    block(this.shouldBeFailure().exception)
}
