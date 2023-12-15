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

package io.github.airflux.functional.collection

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class NonEmptyListTest : FreeSpec() {

    init {

        "The `NonEmptyList` type" - {

            "when only one element is passed to create an instance of the type" - {
                val values = NonEmptyList(FIRST)

                "then the new instance of the type should only contain the passed element" {
                    values.toList() shouldContainExactly listOf(FIRST)
                }
            }

            "when a few element is passed to create an instance of the type" - {
                val values = NonEmptyList(FIRST, SECOND)

                "then the new instance of the type should only contain the passed elements in the order in which they were passed" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND)
                }
            }

            "when a head element and empty tail list are passed to create an instance of the type" - {
                val values = NonEmptyList(FIRST, emptyList())

                "then the new instance of the type should only contain the passed head element" {
                    values.toList() shouldContainExactly listOf(FIRST)
                }
            }

            "when a head element and non-empty tail list are passed to create an instance of the type" - {
                val values = NonEmptyList(FIRST, listOf(SECOND, THIRD))

                "then the new instance of the type should all contain the passed elements in the order in which they were passed" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when an empty list is passed to create an instance of the type" - {
                val values = NonEmptyList.valueOf(emptyList<Int>())

                "then should return the null value" {
                    values.shouldBeNull()
                }
            }

            "when a non-empty list is passed to create an instance of the type" - {
                val values =
                    NonEmptyList.valueOf(listOf(FIRST, SECOND, THIRD))

                "then the new instance of the type should contain all the elements from the list in the order in which they were passed" {
                    values.shouldNotBeNull()
                    values.toList() shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when a new element is added to the instance of the type" - {
                val values = NonEmptyList(FIRST) + SECOND

                "then the new instance of the type should contain the original elements and the passed element in the order in which they were passed" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND)
                }
            }

            "when a list of elements is added to the instance of the type" - {
                val values = NonEmptyList(FIRST) + listOf(SECOND, THIRD)

                "then the new instance of the type should contain elements from the original instance and the passed elements in the order in which they were passed" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when another instance of the type is added to the instance of the type" - {
                val values =
                    NonEmptyList(FIRST) + NonEmptyList(SECOND, THIRD)

                "then the new instance of the type should contain elements from both instances in the order in which they were in the originals" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when calling the `size` property" - {
                val values = NonEmptyList(FIRST, SECOND)

                "then should return the number of elements" {
                    values.size shouldBe 2
                }
            }

            "when calling the `iterator()` function" - {
                val values: NonEmptyList<Int> = NonEmptyList(FIRST, SECOND)

                "then should return an iterator over all elements from the instance" {
                    values.iterator().asSequence().toList() shouldContainExactly listOf(FIRST, SECOND)
                }
            }

            "when calling the `toList()` function" - {
                val values = NonEmptyList(FIRST, SECOND)

                "then should return the list with all elements from the instance" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND)
                }
            }

            "when calling the `getOrNull` function" - {
                val values = NonEmptyList(FIRST, SECOND)

                "when the index is within the valid range" - {
                    val value = values.getOrNull(0)

                    "then should return the value by index" {
                        value shouldBe FIRST
                    }
                }

                "when the index is not in the valid range" - {
                    val value = values.getOrNull(values.size)

                    "then should return the null value" {
                        value.shouldBeNull()
                    }
                }
            }

            "when calling the `any` function" - {
                val values = NonEmptyList(FIRST, SECOND)

                "when any element satisfy the predicate condition" - {
                    val predicate: (Int) -> Boolean = { it == SECOND }

                    "then the `any` method should return true" {
                        values.any(predicate) shouldBe true
                    }
                }

                "when all elements does not satisfy the predicate condition" - {
                    val predicate: (Int) -> Boolean = { it == THIRD }

                    "then the `any` method should return false" {
                        values.any(predicate) shouldBe false
                    }
                }
            }

            "when calling the `all` function" - {
                val values = NonEmptyList(FIRST, SECOND)

                "when all elements satisfy the predicate condition" - {
                    val predicate: (Int) -> Boolean = { it <= SECOND }

                    "then the `all` method should return true" {
                        values.all(predicate) shouldBe true
                    }
                }

                "when some element does not satisfy the predicate condition" - {
                    val predicate: (Int) -> Boolean = { it < SECOND }

                    "then the `all` method should return false" {
                        values.all(predicate) shouldBe false
                    }
                }
            }

            "when calling the `contains` function" - {
                val values = NonEmptyList(FIRST, SECOND)

                "when the list contains a value" - {

                    "then method should return true" {
                        (FIRST in values) shouldBe true
                    }
                }

                "when the list does not contain a value" - {

                    "then method should return false" {
                        (THIRD in values) shouldBe false
                    }
                }
            }

            "when calling the `map` function" - {
                val values = NonEmptyList(FIRST, SECOND).map { it + 1 }

                "then should return the list of transformed values" {
                    values.toList() shouldContainExactly listOf(FIRST + 1, SECOND + 1)
                }
            }

            "when calling the `flatMap` function" - {
                val values = NonEmptyList(FIRST, SECOND).flatMap { NonEmptyList(it, it + 1) }

                "then should return the list of transformed values" {
                    values.toList() shouldContainExactly listOf(FIRST, FIRST + 1, SECOND, SECOND + 1)
                }
            }
        }
    }

    internal companion object {
        private const val FIRST = 1
        private const val SECOND = 2
        private const val THIRD = 3
    }
}
