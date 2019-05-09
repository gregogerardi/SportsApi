package sports.model

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*

//constant
private const val SPORT_1_NAME = "sport1"
private const val SPORT_2_NAME = "sport2"
private const val HASHCODE_SCHEDULE_1 = 1
private const val HASHCODE_SCHEDULE_2 = 2

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SportTests {

    //SUT
    private lateinit var sport1: Sport
    private lateinit var sport2: Sport

    //mock
    private lateinit var schedulesForSport1: List<Schedule>
    private lateinit var schedulesForSport2: List<Schedule>


    @BeforeAll
    fun initBeforeAll() {
        schedulesForSport1 = mockk()
        schedulesForSport2 = mockk()
        every { schedulesForSport1.equals(schedulesForSport1) } returns true
        every { schedulesForSport1.equals(schedulesForSport2) } returns false
        every { schedulesForSport2.equals(schedulesForSport1) } returns false
        every { schedulesForSport2.equals(schedulesForSport2) } returns true
        every { schedulesForSport1.hashCode() } returns HASHCODE_SCHEDULE_1
        every { schedulesForSport2.hashCode() } returns HASHCODE_SCHEDULE_2
        sport1 = Sport(SPORT_1_NAME, schedulesForSport1)
        sport2 = Sport(SPORT_2_NAME, schedulesForSport2)
    }

    @Nested
    inner class TestsForEquals {
        @Test
        fun `equals() by name for sports with different schedules`() {
            Assertions.assertEquals(sport1, Sport(SPORT_1_NAME, schedulesForSport2))
        }

        @Test
        fun `equals() for sports with same schedules but different names`() {
            Assertions.assertNotEquals(sport1, Sport(SPORT_2_NAME, schedulesForSport1))
        }
    }

    @Nested
    inner class TestsForHashcode {
        @Test
        fun `hashcode() by name for sports with different schedules`() {
            Assertions.assertEquals(sport1.hashCode(), Sport(SPORT_1_NAME, schedulesForSport2).hashCode())
        }

        @Test
        fun `hashcode() for sports with same schedules but different names`() {
            Assertions.assertNotEquals(sport1.hashCode(), Sport(SPORT_2_NAME, schedulesForSport1).hashCode())
        }
    }
}