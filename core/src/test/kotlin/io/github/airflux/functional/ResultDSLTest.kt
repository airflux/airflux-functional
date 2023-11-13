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

internal class ResultDSLTest : FreeSpec() {

    init {
        "The Result DSL" - {

            "when all functions involved are executed successfully" - {
                fun first(): Result<Int, Error> = 1.success()
                fun second(): Result<Int, Error> = 2.success()

                "then binding should return a successful value" {
                    val result = Result {
                        val (a) = first()
                        val (b) = second()
                        a + b
                    }

                    result.shouldBeSuccess()
                    result.value shouldBe 3
                }
            }

            "when some function involved is returned failure" - {
                fun first(): Result<Int, Error> = 1.success()
                fun second(): Result<Int, Error> = Error.First.error()
                fun third(): Result<Int, Error> = Error.Second.error()

                "then binding should return a first failure" {
                    val result = Result {
                        val (a) = first()
                        val (b) = second()
                        val (c) = third()
                        a + b + c
                    }

                    result.shouldBeError()
                    result.cause shouldBe Error.First
                }
            }
        }
    }

    internal sealed class Error {
        data object First : Error()
        data object Second : Error()
    }
}
