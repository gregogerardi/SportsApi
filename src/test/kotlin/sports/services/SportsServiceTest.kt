package sports.services

import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import sports.model.repository.SportsRepository

private const val ANY_INT = 1
private const val ANY_STRING = ""


/**
 * In this simple example of Api, our service adds just de functionality for async calls, delegating every
 * data's related feature to the [SportsRepository]. To avoid duplicated code we don't incorporate such tests
 * here. For testings of that features refer to [SportsRepository] tests
 *
 *@see sports.model.repository.SportsMongoDBTest
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SportsServiceTest {

    //mock
    private lateinit var sportsRepository: SportsRepository

    //SUT
    private lateinit var sportsService: SportsService

    @BeforeAll
    fun init() {
        sportsRepository = mockk()
        sportsService = SportsService(sportsRepository)
    }

    @Nested
    inner class TestsForAsync {
        @Test
        fun `getFutureTask() for any task actually invoke the task`() {
            var invoked = false
            sportsService.async { invoked = true }
            Thread.sleep(3000)
            assert(invoked)
        }

        @Test
        fun `async() for any task returns a future for the right type`() {
            val intFuture = sportsService.async { ANY_INT }
            val stringFuture = sportsService.async { ANY_STRING }
            intFuture.setHandler { assert(it.result() is Int) }
            stringFuture.setHandler { assert(it.result() is String) }
        }

        @Test
        fun `async() for any task that throws an expection`() {
            var futureComplete = true
            val future = sportsService.async { throw Exception() }
            future.setHandler { if (it.failed()) futureComplete = false }
            Thread.sleep(3000)
            assert(!futureComplete)
        }

        @Test
        fun `async() for any task that completes`() {
            var futureComplete = false
            val future = sportsService.async {}
            future.setHandler { if (it.succeeded()) futureComplete = true }
            Thread.sleep(3000)
            assert(futureComplete)
        }
    }

}