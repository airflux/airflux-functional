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

internal class ResultDSLTest : FreeSpec() {

    init {
        "The DSL" - {

            "the function `Result`" - {

                "when using one level of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Result<Int, Error> = FIRST_VALUE.success()
                        fun second(): Result<Int, Error> = SECOND_VALUE.success()

                        "when the execution result of a block is successful" - {

                            "then should return a successful value" {
                                val result: Result<Int, Error> = Result {
                                    val (a) = first()
                                    val (b) = second()
                                    a + b
                                }

                                result shouldBeSuccess FIRST_VALUE + SECOND_VALUE
                            }
                        }

                        "when in a block calling the `raise` method" - {

                            "then should return a failure value" {
                                val result = Result {
                                    val (a) = first()
                                    val (b) = second()
                                    if (b == SECOND_VALUE) raise(Error.First)
                                    a + b
                                }

                                result shouldBeError Error.First
                            }
                        }
                    }

                    "when some function returns a failure" - {
                        fun first(): Result<Int, Error> = FIRST_VALUE.success()
                        fun second(): Result<Int, Error> = Error.First.error()
                        fun third(): Result<Int, Error> = Error.Second.error()

                        "then should return a first returned failure" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (c) = third()
                                a + b + c
                            }

                            result shouldBeError Error.First
                        }
                    }
                }

                "when using a few levels of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Result<Int, Error> = FIRST_VALUE.success()
                        fun second(): Result<Int, Error> = SECOND_VALUE.success()
                        fun third(): Result<String, Error> = "3".success()

                        "then should return a successful value" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result shouldBeSuccess 6
                        }
                    }

                    "when some function at an internal nesting level returns an error" - {
                        fun first(): Result<Int, Error> = FIRST_VALUE.success()
                        fun second(): Result<Int, Error> = SECOND_VALUE.success()
                        fun third(): Result<String, Error> = Error.First.error()

                        "then should return failure of an internal nesting level" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result shouldBeError Error.First
                        }
                    }

                    "when every function at all nesting levels returns an error" - {
                        fun first(): Result<Int, Error> = FIRST_VALUE.success()
                        fun second(): Result<Int, Error> = Error.First.error()
                        fun third(): Result<String, Error> = Error.Second.error()

                        "then should return failure of a top-level" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result shouldBeError Error.First
                        }
                    }
                }

                "when a function returns a successful" - {
                    fun first(): Result<Int, Error> = FIRST_VALUE.success()

                    "then calling the `raise` function should have no effect" {
                        val result: Result<Int, Error> = Result {
                            first().raise()
                            SECOND_VALUE
                        }

                        result shouldBeSuccess SECOND_VALUE
                    }
                }

                "when a function returns an error" - {
                    fun first(): Result<Int, Error> = Error.First.error()

                    "then calling the `raise` function should return an error" {
                        val result: Result<Int, Error> = Result {
                            first().raise()
                            SECOND_VALUE
                        }

                        result shouldBeError Error.First
                    }
                }
            }
        }
    }

    internal sealed class Error {
        data object First : Error()
        data object Second : Error()
    }

    private companion object {
        private const val FIRST_VALUE = 1
        private const val SECOND_VALUE = 2
    }
}
