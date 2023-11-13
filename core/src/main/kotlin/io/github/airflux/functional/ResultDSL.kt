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
import kotlin.experimental.ExperimentalTypeInference

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
public inline fun <T, E> Result(@BuilderInference block: Result.Builder<E>.() -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val raise = ResultBuilder<E>()
    return try {
        block(raise)
    } catch (expected: ResultRaiseException) {
        expected.failureOrRethrow(raise)
    }
}

@PublishedApi
internal class ResultBuilder<E> : Result.Builder<E> {

    override fun <V> Result<V, E>.bind(): V = if (isSuccess()) value else raise(this)

    override fun <T> Result<T, E>.component1(): T = bind()

    private fun raise(error: Result.Error<E>): Nothing {
        throw ResultRaiseException(error, this)
    }
}

internal class ResultRaiseException(val failure: Any, val raise: ResultBuilder<*>) : IllegalStateException()

@PublishedApi
internal fun <E> ResultRaiseException.failureOrRethrow(raise: ResultBuilder<E>): Result.Error<E> =
    if (this.raise === raise)
        @Suppress("UNCHECKED_CAST")
        failure as Result.Error<E>
    else
        throw this
