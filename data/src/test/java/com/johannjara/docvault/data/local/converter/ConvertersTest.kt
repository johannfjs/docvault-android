package com.johannjara.docvault.data.local.converter

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class ConvertersTest {

    @Test
    fun `fromList should convert List of Long to JSON string`() {
        val list = listOf(1L, 2L, 3L)
        val result = Converters.fromList(list)
        result shouldBeEqualTo "[1,2,3]"
    }

    @Test
    fun `toList should convert JSON string to List of Long`() {
        val json = "[1,2,3]"
        val result = Converters.toList(json)
        result shouldBeEqualTo listOf(1L, 2L, 3L)
    }

    @Test
    fun `fromList with empty list should return empty JSON array`() {
        val list = emptyList<Long>()
        val result = Converters.fromList(list)
        result shouldBeEqualTo "[]"
    }

    @Test
    fun `toList with empty JSON array should return empty list`() {
        val json = "[]"
        val result = Converters.toList(json)
        result shouldBeEqualTo emptyList<Long>()
    }
}
