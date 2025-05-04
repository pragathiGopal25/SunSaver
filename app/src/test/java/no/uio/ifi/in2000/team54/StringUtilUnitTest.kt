package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.util.isNumber
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringUtilUnitTest {

    @Test
    fun isNumberShouldReturnTrue() {
        // arrange
        val string = "123034123412341235123585"

        // act
        val result = string.isNumber()

        // assert
        assertTrue(result)
    }

    @Test
    fun isNumberShouldReturnFalseEdge() {
        // arrange
        val string = "%"

        // act
        val result = string.isNumber()

        // assert
        assertFalse(result)
    }

    @Test
    fun isNumberShouldReturnFalse() {
        // arrange
        val string = "te1st"

        // act
        val result = string.isNumber()

        // assert
        assertFalse(result)
    }
}