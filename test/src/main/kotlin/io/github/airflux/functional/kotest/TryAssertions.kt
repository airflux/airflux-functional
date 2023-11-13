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
import io.kotest.assertions.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun <T> Try<T>.shouldBeSuccess(): Try.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is Try.Success<T>)
    }

    val message =
        "The result type is not as expected. Expected type: `Try.Success`, actual type: `Try.Failure` ($this)."
    return if (isSuccess()) this else throw failure(message = message)
}

@OptIn(ExperimentalContracts::class)
public fun <T> Try<T>.shouldBeError(): Try.Failure {
    contract {
        returns() implies (this@shouldBeError is Try.Failure)
    }

    val message =
        "The result type is not as expected. Expected type: `Try.Failure`,  actual type: `Try.Success` ($this)."
    return if (isError()) this else throw failure(message = message)
}
