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

import io.github.airflux.functional.Result
import io.github.airflux.functional.isError
import io.github.airflux.functional.isSuccess
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <reified T> Result<T, *>.shouldBeSuccess(): Result.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is Result.Success<T>)
    }

    if (this.isError()) {
        errorCollector.collectOrThrow(
            failure(
                expected = Result.Success::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Success, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as Result.Success<T>
}

public inline infix fun <reified T> Result<T, *>.shouldBeSuccess(expected: T) {
    this.shouldBeSuccess().value shouldBe expected
}

public inline infix fun <reified T> Result<T, *>.shouldBeSuccess(block: (T) -> Unit) {
    block(this.shouldBeSuccess().value)
}

@OptIn(ExperimentalContracts::class)
public inline fun <reified E> Result<*, E>.shouldBeError(): Result.Error<E> {
    contract {
        returns() implies (this@shouldBeError is Result.Error<E>)
    }

    if (this.isSuccess()) {
        errorCollector.collectOrThrow(
            failure(
                expected = Result.Error::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Error, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as Result.Error<E>
}

public inline infix fun <reified E> Result<*, E>.shouldBeError(expected: E) {
    this.shouldBeError().cause shouldBe expected
}

public inline infix fun <reified E> Result<*, E>.shouldBeError(block: (E) -> Unit) {
    block(this.shouldBeError().cause)
}
