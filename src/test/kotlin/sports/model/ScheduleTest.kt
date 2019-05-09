package sports.model

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//constants
private const val HASHCODE_TIME_1 = 10
private const val HASHCODE_TIME_2 = 20

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduleTests {


    //mocks
    private lateinit var time1: Time
    private lateinit var time2: Time

    //SUT
    private lateinit var schedule1: Schedule
    private lateinit var schedule2: Schedule

    @BeforeAll
    fun init() {
        time1 = mockk()
        time2 = mockk()
        every { time1.equals(time1) } returns true
        every { time1.equals(time2) } returns false
        every { time2.equals(time2) } returns true
        every { time2.equals(time1) } returns false
        every { time1.hashCode() } returns HASHCODE_TIME_1
        every { time2.hashCode() } returns HASHCODE_TIME_2
    }

    @Nested
    inner class TestsForEquals {
        @Test
        fun `equals()for schedules with different fromHour times`() {
            schedule1 = Schedule(time1, time2, Day.MONDAY)
            schedule2 = Schedule(time2, time2, Day.MONDAY)
            assertNotEquals(schedule1, schedule2)
        }

        @Test
        fun `equals()for schedules with different toHour times`() {
            schedule1 = Schedule(time2, time1, Day.MONDAY)
            schedule2 = Schedule(time2, time2, Day.MONDAY)
            assertNotEquals(schedule1, schedule2)
        }

        @Test
        fun `equals()for schedules with different days`() {
            schedule1 = Schedule(time1, time2, Day.MONDAY)
            schedule2 = Schedule(time1, time2, Day.TUESDAY)
            assertNotEquals(schedule1, schedule2)
        }

        @Test
        fun `equals()for equal schedules`() {
            schedule1 = Schedule(time1, time2, Day.MONDAY)
            schedule2 = Schedule(time1, time2, Day.MONDAY)
            assertEquals(schedule1, schedule2)
        }
    }

    @Nested
    inner class TestsForHashcode {
        @Test
        fun `equals()for schedules with different fromHour times`() {
            schedule1 = Schedule(time1, time2, Day.MONDAY)
            schedule2 = Schedule(time2, time2, Day.MONDAY)
            assertNotEquals(schedule1.hashCode(), schedule2.hashCode())
        }

        @Test
        fun `hashcode()for schedules with different toHour times`() {
            schedule1 = Schedule(time2, time1, Day.MONDAY)
            schedule2 = Schedule(time2, time2, Day.MONDAY)
            assertNotEquals(schedule1.hashCode(), schedule2.hashCode())
        }

        @Test
        fun `hashcode()for schedules with different days`() {
            schedule1 = Schedule(time1, time2, Day.MONDAY)
            schedule2 = Schedule(time1, time2, Day.TUESDAY)
            assertNotEquals(schedule1.hashCode(), schedule2.hashCode())
        }
        @Test
        fun `hashcode()for equal schedules`() {
            schedule1 = Schedule(time1, time2, Day.MONDAY)
            schedule2 = Schedule(time1, time2, Day.MONDAY)
            assertEquals(schedule1.hashCode(), schedule2.hashCode())
        }
    }
}