package sports.model

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//constants
private const val HOUR_1 = 1
private const val HOUR_2 = 2
private const val MINUTES_1 = 11
private const val MINUTES_2 = 12

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimeTests {

    //SUT
    private lateinit var time1: Time
    private lateinit var time2: Time

    @Nested
    inner class TestsForEquals {
        @Test
        fun `equals()for times with different hours`() {
            time1 = Time(HOUR_1, MINUTES_1)
            time2 = Time(HOUR_2, MINUTES_1)
            assert(time1!=time2)
        }

        @Test
        fun `equals()for times with different minutes`() {
            time1 = Time(HOUR_1, MINUTES_1)
            time2 = Time(HOUR_1, MINUTES_2)
            assert(time1!=time2)
        }

        @Test
        fun `equals()for equal times`() {
            time1 = Time(HOUR_1, MINUTES_1)
            time2 = Time(HOUR_1, MINUTES_1)
            assert(time1==time2)
        }
    }

    @Nested
    inner class TestsForHashcode {
        @Test
        fun `hashcode()for times with different hours`() {
            time1 = Time(HOUR_1, MINUTES_1)
            time2 = Time(HOUR_2, MINUTES_1)
            assert(time1.hashCode()!=time2.hashCode())
        }

        @Test
        fun `hashcode()for times with different minutes`() {
            time1 = Time(HOUR_1, MINUTES_1)
            time2 = Time(HOUR_1, MINUTES_2)
            assert(time1.hashCode()!=time2.hashCode())
        }

        @Test
        fun `hashcode()for equal times`() {
            time1 = Time(HOUR_1, MINUTES_1)
            time2 = Time(HOUR_1, MINUTES_1)
            assert(time1.hashCode()==time2.hashCode())
        }
    }
}