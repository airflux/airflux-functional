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

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <L, R> Either(block: Either.Raise<L>.() -> R): Either<L, R> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return EitherWith { block().right() }
}

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <L, R> EitherWith(block: Either.Raise<L>.() -> Either<L, R>): Either<L, R> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val raise = EitherRaise<L>()
    return try {
        block(raise)
    } catch (expected: RaiseException) {
        expected.failureOrRethrow(raise)
    }
}

@PublishedApi
internal class EitherRaise<L> : Either.Raise<L> {

    override fun <R> Either<L, R>.bind(): R = if (isRight()) get else raise(this)

    override fun raise(error: L): Nothing {
        raise(Either.Left(error))
    }

    override fun <R> Either<L, R>.raise() {
        if (isRight()) Unit else raise(this)
    }

    private fun raise(error: Either.Left<L>): Nothing {
        throw RaiseException(error, this)
    }
}
