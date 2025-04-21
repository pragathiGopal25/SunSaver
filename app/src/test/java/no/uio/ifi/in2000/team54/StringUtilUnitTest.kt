package no.uio.ifi.in2000.team54

import no.uio.ifi.in2000.team54.util.isNumber
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringUtilUnitTest {

    @Test
    fun isNumberShouldReturnTrue() {
        //TODO arrange
        val string = "123034123412341235123585"

        //TODO act
        val result = string.isNumber()

        //TODO assert
        assertTrue(result)
    }

    @Test
    fun isNumberShouldReturnFalseEdge() {
        //TODO arrange
        val string = "%"

        //TODO act
        val result = string.isNumber()

        //TODO assert
        assertFalse(result)
    }

    @Test
    fun isNumberShouldReturnFalse() {
        //TODO arrange
        val string = "te1st"

        //TODO act
        val result = string.isNumber()

        //TODO assert
        assertFalse(result)
    }
}