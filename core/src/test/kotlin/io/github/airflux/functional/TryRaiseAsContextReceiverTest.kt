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

import io.github.airflux.functional.kotest.shouldBeFailure
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class TryRaiseAsContextReceiverTest : FreeSpec() {

    init {
        "The `Try` type in context" - {

            "when the `raise` function was not called" {
                val result = Try {
                    find(USER_ID)
                }

                result shouldBeSuccess User(USER_ID, USER_NAME)
            }

            "when the `raise` function was called" {
                val result = Try {
                    find(UNKNOWN_USER_ID)
                }

                result shouldBeFailure IllegalStateException(STORAGE_ERROR)
            }
        }
    }

    context (Try.Raise)
    private fun find(id: Int): User =
        if (id == USER_ID)
            User(USER_ID, USER_NAME)
        else
            raise(IllegalStateException(STORAGE_ERROR))

    internal data class User(val id: Int, val name: String)

    private companion object {
        private const val USER_ID = 1
        private const val UNKNOWN_USER_ID = 2
        private const val USER_NAME = "user"

        private const val STORAGE_ERROR = "The error of storage."
    }
}
