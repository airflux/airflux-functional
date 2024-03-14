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

@Suppress("TooManyFunctions")
@JvmInline
public value class NonEmptyList<out T> @PublishedApi internal constructor(@PublishedApi internal val items: List<T>) {

    public val size: Int
        get() = items.size

    public fun toList(): List<T> = items

    public operator fun iterator(): Iterator<T> = items.iterator()

    public fun getOrNull(index: Int): T? = items.getOrNull(index)

    public infix operator fun contains(value: @UnsafeVariance T): Boolean = items.contains(value)

    public fun exists(predicate: (T) -> Boolean): Boolean = any(predicate)

    public inline fun any(predicate: (T) -> Boolean): Boolean = items.any(predicate)

    public inline fun all(predicate: (T) -> Boolean): Boolean = items.all(predicate)

    public inline fun <R> map(transform: (T) -> R): NonEmptyList<R> = NonEmptyList(items.map(transform))

    public inline fun <R> flatMap(transform: (T) -> NonEmptyList<R>): NonEmptyList<R> =
        NonEmptyList(items.flatMap { transform(it).items })

    public operator fun plus(item: @UnsafeVariance T): NonEmptyList<T> = NonEmptyList(items + item)

    public operator fun plus(items: Iterable<@UnsafeVariance T>): NonEmptyList<T> = NonEmptyList(this.items + items)

    public operator fun plus(other: NonEmptyList<@UnsafeVariance T>): NonEmptyList<T> = plus(other.items)

    public companion object {

        @JvmStatic
        public operator fun <T> invoke(head: T, vararg tail: T): NonEmptyList<T> = invoke(head, tail.toList())

        @JvmStatic
        public operator fun <T> invoke(head: T, tail: List<T>): NonEmptyList<T> = NonEmptyList(
            buildList {
                add(head)
                addAll(tail)
            }
        )

        @JvmStatic
        public fun <T> valueOf(list: List<T>): NonEmptyList<T>? =
            list.takeIf { it.isNotEmpty() }
                ?.let { NonEmptyList(it) }
    }
}
