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

import io.github.airflux.functional.kotest.shouldBeLeft
import io.github.airflux.functional.kotest.shouldBeRight
import io.kotest.core.spec.style.FreeSpec

internal class EitherDSLTest : FreeSpec() {

    init {
        "The DSL" - {

            "the function `Either`" - {

                "when using one level of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Either<Error, Int> = FIRST_VALUE.right()
                        fun second(): Either<Error, Int> = SECOND_VALUE.right()

                        "when the execution result of a block is successful" - {

                            "then should return a successful value" {
                                val either: Either<Error, Int> = Either {
                                    val (a) = first()
                                    val (b) = second()
                                    a + b
                                }

                                either.shouldBeRight(FIRST_VALUE + SECOND_VALUE)
                            }
                        }

                        "when in a block calling the `raise` method" - {

                            "then should return a failure value" {
                                val either = Either {
                                    val (a) = first()
                                    val (b) = second()
                                    if (b == SECOND_VALUE) raise(Error.First)
                                    a + b
                                }

                                either shouldBeLeft Error.First
                            }
                        }
                    }

                    "when some function returns a failure" - {
                        fun first(): Either<Error, Int> = FIRST_VALUE.right()
                        fun second(): Either<Error, Int> = Error.First.left()
                        fun third(): Either<Error, Int> = Error.Second.left()

                        "then should return a first returned failure" {
                            val either = Either {
                                val (a) = first()
                                val (b) = second()
                                val (c) = third()
                                a + b + c
                            }

                            either shouldBeLeft Error.First
                        }
                    }
                }

                "when using a few levels of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): Either<Error, Int> = FIRST_VALUE.right()
                        fun second(): Either<Error, Int> = SECOND_VALUE.right()
                        fun third(): Either<Error, String> = "3".right()

                        "then should return a successful value" {
                            val either = Either {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Either {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            either shouldBeRight 6
                        }
                    }

                    "when some function at an internal nesting level returns an error" - {
                        fun first(): Either<Error, Int> = FIRST_VALUE.right()
                        fun second(): Either<Error, Int> = SECOND_VALUE.right()
                        fun third(): Either<Error, String> = Error.First.left()

                        "then should return failure of an internal nesting level" {
                            val either = Either {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Either {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            either shouldBeLeft Error.First
                        }
                    }

                    "when every function at all nesting levels returns an error" - {
                        fun first(): Either<Error, Int> = FIRST_VALUE.right()
                        fun second(): Either<Error, Int> = Error.First.left()
                        fun third(): Either<Error, String> = Error.Second.left()

                        "then should return failure of a top-level" {
                            val either = Either {
                                val (a) = first()
                                val (b) = second()
                                val (d) = Either {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            either shouldBeLeft Error.First
                        }
                    }
                }

                "when a function returns a successful" - {
                    fun first(): Either<Error, Int> = FIRST_VALUE.right()

                    "then calling the `raise` function should have no effect" {
                        val either: Either<Error, Int> = Either {
                            first().raise()
                            SECOND_VALUE
                        }

                        either shouldBeRight SECOND_VALUE
                    }
                }

                "when a function returns an error" - {
                    fun first(): Either<Error, Int> = Error.First.left()

                    "then calling the `raise` function should return an error" {
                        val either: Either<Error, Int> = Either {
                            first().raise()
                            SECOND_VALUE
                        }

                        either shouldBeLeft Error.First
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
