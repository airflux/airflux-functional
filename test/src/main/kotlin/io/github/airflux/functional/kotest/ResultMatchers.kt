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

public inline fun <reified T, F> Result<T, F>.getValue(): T = this.shouldBeSuccess().value

@OptIn(ExperimentalContracts::class)
public inline fun <reified T, F> Result<T, F>.shouldBeSuccess(message: (F) -> String = { it.toString() }): Result.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is Result.Success<T>)
    }

    if (this.isError()) {
        val expectedType = Result.Success::class.qualifiedName!!
        val actualType = this::class.qualifiedName!!
        val causeDescription = message(this.cause).makeDescription()
        errorCollector.collectOrThrow(
            failure(
                expected = expectedType,
                actual = actualType,
                failureMessage = "Expected the `$expectedType` type, but got the `$actualType` type$causeDescription"
            )
        )
    }
    return this as Result.Success<T>
}

public inline infix fun <reified T> Result<T, *>.shouldBeSuccess(expected: T) {
    this.shouldBeSuccess().value shouldBe expected
}

public inline fun <reified T, F> Result<T, F>.shouldBeSuccess(expected: T, message: (F) -> String) {
    this.shouldBeSuccess(message).value shouldBe expected
}

public inline infix fun <reified T> Result<T, *>.shouldBeSuccess(block: (T) -> Unit) {
    block(this.shouldBeSuccess().value)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, reified E> Result<T, E>.shouldBeError(
    message: (T) -> String = { it.toString() }
): Result.Error<E> {
    contract {
        returns() implies (this@shouldBeError is Result.Error<E>)
    }

    if (this.isSuccess()) {
        val expectedType = Result.Error::class.qualifiedName!!
        val actualType = this::class.simpleName!!
        val causeDescription = message(this.value).makeDescription()
        errorCollector.collectOrThrow(
            failure(
                expected = expectedType,
                actual = actualType,
                failureMessage = "Expected the `$expectedType` type, but got the `$actualType` type$causeDescription"
            )
        )
    }
    return this as Result.Error<E>
}

public inline infix fun <reified E> Result<*, E>.shouldBeError(expected: E) {
    this.shouldBeError().cause shouldBe expected
}

public inline fun <T, reified E> Result<T, E>.shouldBeError(expected: E, message: (T) -> String) {
    this.shouldBeError(message).cause shouldBe expected
}

public inline infix fun <reified E> Result<*, E>.shouldBeError(block: (E) -> Unit) {
    block(this.shouldBeError().cause)
}

@PublishedApi
internal fun String.makeDescription(): String = escape()
    .takeIf { it.isNotBlank() }
    ?.let { " ($it)." }
    ?: "."

@PublishedApi
internal fun String.escape(): String = this.replace(System.lineSeparator(), ". ")
