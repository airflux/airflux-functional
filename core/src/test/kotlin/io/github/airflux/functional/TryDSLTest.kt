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
import io.kotest.matchers.types.shouldBeInstanceOf

internal class TryDSLTest : FreeSpec() {

    init {
        "The DSL" - {

            "the function `run`" - {

                "when using one level of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Try<Int> = Try.Success(1)
                        fun second(): Try<Int> = Try.Success(0)

                        "when the execution result of a block is successful" - {

                            "then the binding should return a successful value" {
                                val result: Try<Int> = Try.run {
                                    val (a) = first()
                                    val (b) = second()
                                    a + b
                                }

                                result.shouldBeSuccess()
                                result.result shouldBe 1
                            }
                        }

                        "when the execution result of a block is failure" - {

                            "then the binding should return a failure value" {
                                val result: Try<Int> = Try.run {
                                    val (a) = first()
                                    val (b) = second()
                                    a / b
                                }

                                result.shouldBeError()
                                result.exception.shouldBeInstanceOf<ArithmeticException>()
                            }
                        }

                        "when the execution result of a block is raise" - {

                            "then the binding should return a failure value" {
                                val result = Try.run {
                                    val (a) = first()
                                    val (b) = second()
                                    if (b == 0) raise(IllegalArgumentException("b == 0")) else a / b
                                }

                                result.shouldBeError()
                                result.exception.shouldBeInstanceOf<IllegalArgumentException>()
                            }
                        }
                    }

                    "when some function returns a failure" - {
                        fun first(): Try<Int> = Try.Success(1)
                        fun second(): Try<Int> = Try.Failure(FirstException)
                        fun third(): Try<Int> = Try.Failure(SecondException)

                        "then the binding should return a first returned failure" {
                            val result = Try.run {
                                val (a) = first()
                                val (b) = second()
                                val (c) = third()
                                a + b + c
                            }

                            result.shouldBeError()
                            result.exception shouldBe FirstException
                        }
                    }
                }

                "when using a few levels of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Try<Int> = Try.Success(1)
                        fun second(): Try<Int> = Try.Success(2)
                        fun third(): Try<String> = Try.Success("3")

                        "then the binding should return a successful value" {
                            val result = Try.run {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Try.run {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result.shouldBeSuccess()
                            result.result shouldBe 6
                        }
                    }

                    "when some function at an internal nesting level returns a failure" - {
                        fun first(): Try<Int> = Try.Success(1)
                        fun second(): Try<Int> = Try.Success(2)
                        fun third(): Try<String> = Try.Failure(FirstException)

                        "then the binding should return failure of an internal nesting level" {
                            val result = Try.run {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Try.run {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result.shouldBeError()
                            result.exception shouldBe FirstException
                        }
                    }

                    "when every function at all nesting levels returns a failure" - {
                        fun first(): Try<Int> = Try.Success(1)
                        fun second(): Try<Int> = Try.Failure(FirstException)
                        fun third(): Try<String> = Try.Failure(SecondException)

                        "then the binding should return failure of a top-level" {
                            val result = Try.run {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Try.run {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result.shouldBeError()
                            result.exception shouldBe FirstException
                        }
                    }
                }
            }
        }
    }

    internal object FirstException : RuntimeException()
    internal object SecondException : RuntimeException()
}