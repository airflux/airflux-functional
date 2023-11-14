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

package io.github.airflux.functional

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <T> Try.Companion.run(block: Try.Raise.() -> T): Try<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return runWith { Try.Success(block()) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> Try.Companion.runWith(block: Try.Raise.() -> Try<T>): Try<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val raise = TryRaise()
    return try {
        block(raise)
    } catch (expected: TryRaiseException) {
        expected.failureOrRethrow(raise)
    } catch (expected: Throwable) {
        if (expected.isFatal()) throw expected else Try.Failure(expected)
    }
}

@PublishedApi
internal class TryRaise : Try.Raise {

    override fun <T> Try<T>.bind(): T = if (isSuccess()) result else raise(this)

    override fun raise(exception: Throwable): Nothing {
        raise(Try.Failure(exception))
    }

    private fun <T> raise(failure: Try<T>): Nothing {
        throw TryRaiseException(failure, this)
    }
}

internal class TryRaiseException(val failure: Any, val raise: TryRaise) : IllegalStateException()

@PublishedApi
internal fun <T> TryRaiseException.failureOrRethrow(raise: TryRaise): Try<T> =
    if (this.raise === raise)
        @Suppress("UNCHECKED_CAST")
        failure as Try<T>
    else
        throw this