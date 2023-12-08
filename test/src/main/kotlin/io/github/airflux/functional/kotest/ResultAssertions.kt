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
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beSuccess(): Matcher<Result<*, *>> =
    object : Matcher<Result<*, *>> {
        override fun test(value: Result<*, *>): MatcherResult =
            typeMatch(expected = Result.Success::class, actual = value)
    }

public fun <T> beSuccess(expected: T): Matcher<Result<T, *>> =
    object : Matcher<Result<T, *>> {
        override fun test(value: Result<T, *>): MatcherResult =
            valueMatch(expected = Result.Success(expected), actual = value)
    }

@OptIn(ExperimentalContracts::class)
public fun <T> Result<T, *>.shouldBeSuccess(): Result.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is Result.Success<T>)
    }
    this should beSuccess()
    return this as Result.Success
}

public infix fun <T> Result<T, *>.shouldBeSuccess(value: T) {
    this should beSuccess(value)
}

public inline infix fun <T> Result<T, *>.shouldBeSuccess(block: (T) -> Unit) {
    block(this.shouldBeSuccess().value)
}

public fun beFailure(): Matcher<Result<*, *>> =
    object : Matcher<Result<*, *>> {
        override fun test(value: Result<*, *>): MatcherResult =
            typeMatch(expected = Result.Error::class, actual = value)
    }

public fun <E> beFailure(expected: E): Matcher<Result<*, E>> =
    object : Matcher<Result<*, E>> {
        override fun test(value: Result<*, E>): MatcherResult =
            valueMatch(expected = Result.Error(expected), actual = value)
    }

@OptIn(ExperimentalContracts::class)
public fun <E> Result<*, E>.shouldBeError(): Result.Error<E> {
    contract {
        returns() implies (this@shouldBeError is Result.Error<E>)
    }
    this should beFailure()
    return this as Result.Error<E>
}

public infix fun <E> Result<*, E>.shouldBeError(expected: E) {
    this should beFailure(expected)
}

public inline infix fun <E> Result<*, E>.shouldBeError(block: (E) -> Unit) {
    block(this.shouldBeError().cause)
}

private fun <T, E> valueMatch(expected: Result<T, E>, actual: Result<T, E>): MatcherResult =
    comparableMatcherResult(
        passed = expected == actual,
        actual = actual.toString(),
        expected = expected.toString()
    )

private fun <T, E, K : Result<T, E>> typeMatch(expected: KClass<K>, actual: Result<T, E>): MatcherResult =
    comparableMatcherResult(
        passed = expected.isInstance(actual),
        actual = actual::class.qualifiedName!!,
        expected = expected.qualifiedName!!
    )

private fun comparableMatcherResult(passed: Boolean, actual: String, expected: String) =
    ComparableMatcherResult(
        passed = passed,
        failureMessageFn = { "" },
        negatedFailureMessageFn = { "not " },
        actual = actual,
        expected = expected
    )
