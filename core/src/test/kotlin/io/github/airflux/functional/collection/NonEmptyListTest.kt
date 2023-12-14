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
                val values = NonEmptyList.valueOf(listOf(FIRST, SECOND, THIRD))

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
                val values = NonEmptyList(FIRST) + NonEmptyList(SECOND, THIRD)

                "then the new instance of the type should contain elements from both instances in the order in which they were in the originals" {
                    values.toList() shouldContainExactly listOf(FIRST, SECOND, THIRD)
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

            "when a predicate of the `exists` method for a list returns true" - {
                val predicate: (Int) -> Boolean = { it == SECOND }

                "then the `exists` method should return true" {
                    val values = NonEmptyList(FIRST, SECOND, THIRD)
                    values.exists(predicate) shouldBe true
                }
            }

            "when a predicate of the `exists` method for a list returns false" - {
                val predicate: (Int) -> Boolean = { it == THIRD }
                val values = NonEmptyList(FIRST, SECOND)

                "then the `exists` method should return false" {
                    values.exists(predicate) shouldBe false
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
