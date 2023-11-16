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

import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ResultRaiseAsContextReceiverTest : FreeSpec() {

    init {
        "The `Result` type in context" - {

            "when the `raise` function was not called" {
                val result = Result {
                    getUser(USER_ID)
                }

                result.shouldBeSuccess()
                result.value shouldBe User(USER_ID, USER_NAME)
            }

            "when the `raise` function was called" {
                val result = Result {
                    getUser(UNKNOWN_USER_ID)
                }

                result.shouldBeError()
                result.cause shouldBe Errors.UserNotFound
            }
        }
    }

    context (Result.Raise<Errors.UserNotFound>)
    private fun getUser(id: Int): User =
        if (id == USER_ID)
            User(USER_ID, USER_NAME)
        else
            raise(Errors.UserNotFound)

    internal data class User(val id: Int, val name: String)

    private sealed class Errors {
        data object UserNotFound : Errors()
    }

    private companion object {
        private const val USER_ID = 1
        private const val UNKNOWN_USER_ID = 2
        private const val USER_NAME = "user"
    }
}
