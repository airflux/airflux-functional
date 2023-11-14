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
        "The DSL" - {

            "the function `Result`" - {

                "when using one level of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Result<Int, Error> = 1.success()
                        fun second(): Result<Int, Error> = 2.success()

                        "then the binding should return a successful value" {
                            val result: Result<Int, Error> = Result {
                                val (a) = first()
                                val (b) = second()
                                a + b
                            }

                            result.shouldBeSuccess()
                            result.value shouldBe 3
                        }
                    }

                    "when some function returns a failure" - {
                        fun first(): Result<Int, Error> = 1.success()
                        fun second(): Result<Int, Error> = Error.First.error()
                        fun third(): Result<Int, Error> = Error.Second.error()

                        "then the binding should return a first returned failure" {
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

                "when using a few levels of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Result<Int, Error> = 1.success()
                        fun second(): Result<Int, Error> = 2.success()
                        fun third(): Result<String, Error> = "3".success()

                        "then the binding should return a successful value" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result.shouldBeSuccess()
                            result.value shouldBe 6
                        }
                    }

                    "when some function at an internal nesting level returns an error" - {
                        fun first(): Result<Int, Error> = 1.success()
                        fun second(): Result<Int, Error> = 2.success()
                        fun third(): Result<String, Error> = Error.First.error()

                        "then the binding should return failure of an internal nesting level" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result.shouldBeError()
                            result.cause shouldBe Error.First
                        }
                    }

                    "when every function at all nesting levels returns an error" - {
                        fun first(): Result<Int, Error> = 1.success()
                        fun second(): Result<Int, Error> = Error.First.error()
                        fun third(): Result<String, Error> = Error.Second.error()

                        "then the binding should return failure of a top-level" {
                            val result = Result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result.shouldBeError()
                            result.cause shouldBe Error.First
                        }
                    }
                }
            }
        }
    }

    internal sealed class Error {
        data object First : Error()
        data object Second : Error()
    }
}
