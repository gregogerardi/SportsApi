package sports.controller

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import sports.LOCAL_HOST
import sports.SERVER_PORT
import sports.model.*
import sports.services.SportsService
import java.util.*

//constants
private const val SPORT_1_NAME = "sport1"
private const val SPORT_2_NAME = "sport2"
private const val SPORT_3_NAME = "sport3"
private const val EXCEPTION_MESSAGE = "unexpected error"

@Suppress("DEPRECATION")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension::class)
class SportsControllerTest {


    //mock
    private lateinit var sportsService: SportsService

    //data classes
    private lateinit var sport1: Sport
    private lateinit var sport2: Sport
    private lateinit var sport3: Sport

    private lateinit var schedule1: Schedule
    private lateinit var schedule2: Schedule
    private lateinit var schedule3: Schedule
    private lateinit var schedule4: Schedule
    private lateinit var schedule5: Schedule
    private lateinit var schedule6: Schedule
    private lateinit var schedule7: Schedule

    private lateinit var schedulesForSport1: List<Schedule>
    private lateinit var schedulesForSport2: List<Schedule>
    private lateinit var schedulesForSport3: List<Schedule>


    private lateinit var exception: Exception

    //SUT


    @BeforeAll
    fun initTests(vertx: Vertx, context: VertxTestContext) {
        initDataClasses()
        sportsService = mockk()
        val router = SportsController.initRouter(Router.router(vertx), sportsService)
        vertx.createHttpServer().requestHandler(router)
            .listen(SERVER_PORT, LOCAL_HOST) { if (it.succeeded()) context.completeNow() }
        exception = Exception(EXCEPTION_MESSAGE)
    }

    private fun initDataClasses() {
        schedule1 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.MONDAY)
        schedule2 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.TUESDAY)
        schedule3 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.WEDNESDAY)
        schedule4 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.THURSDAY)
        schedule5 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.MONDAY)
        schedule6 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.TUESDAY)
        schedule7 = Schedule(Time(ANY_HOUR, ANY_MIN), Time(ANY_HOUR, ANY_MIN), Day.WEDNESDAY)

        schedulesForSport1 = listOf(schedule1, schedule2)
        schedulesForSport2 = listOf(schedule3, schedule4, schedule5, schedule6, schedule7)
        schedulesForSport3 = listOf()

        sport1 = Sport(SPORT_1_NAME, schedulesForSport1)
        sport2 = Sport(SPORT_2_NAME, schedulesForSport2)
        sport3 = Sport(SPORT_3_NAME, schedulesForSport3)
    }

    @BeforeEach
    fun clear(context: VertxTestContext) {
        clearMocks(sportsService)
        context.completeNow()
    }

    @Nested
    inner class HTTPGet {
        @Nested
        inner class HTTPGetTests {

            @Test
            fun `GET from an empty registry`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<List<Sport>>()
                every { sportsService.async<List<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_OK, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            GSON.fromJson(it.toJson().toString(), Array<Sport>::class.java).toList(),
                            listOf<Sport>()
                        )
                        context.completeNow()
                    }
                }
                request.end()
                future.complete(listOf())
            }

            @Test
            fun `GET from an registry with one Sport`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<List<Sport>>()
                every { sportsService.async<List<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_OK, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            GSON.fromJson(it.toJson().toString(), Array<Sport>::class.java).toList(),
                            listOf(sport1)
                        )
                        context.completeNow()
                    }
                }
                request.end()
                future.complete(listOf(sport1))
            }

            @Test
            fun `GET from an registry with three sports`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<List<Sport>>()
                every { sportsService.async<List<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_OK, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            GSON.fromJson(it.toJson().toString(), Array<Sport>::class.java).toList(),
                            listOf(sport1, sport2, sport3)
                        )
                        context.completeNow()
                    }
                }
                request.end()
                future.complete(listOf(sport1, sport2, sport3))
            }

            @Test
            fun `GET from an registry with an internal error`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<List<Sport>>()
                every { sportsService.async<List<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            it.toString(),
                            exception.toString()
                        )
                        context.completeNow()
                    }
                }
                request.end()
                future.fail(exception)
            }
        }

        @Nested
        inner class HTTPGetASportTests {

            @Test
            fun `GET a sport from an regisgry which not contains it`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Optional<Sport>>()
                every { sportsService.async<Optional<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME").handler {
                    assertEquals(HTTP_NOT_FOUND, it.statusCode())
                    context.completeNow()
                }
                request.end()
                future.complete(Optional.empty())
            }

            @Test
            fun `GET a sport from an registry who contains it`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Optional<Sport>>()
                every { sportsService.async<Optional<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME").handler {
                    assertEquals(HTTP_OK, it.statusCode())
                    it.bodyHandler {
                        assert(GSON.fromJson(it.toJson().toString(), Sport::class.java) == sport1)
                        context.completeNow()
                    }
                }
                request.end()
                future.complete(Optional.of(sport1))
            }

            @Test
            fun `GET a Sport from an registry with an internal error`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Optional<Sport>>()
                every { sportsService.async<Optional<Sport>>(any()) } returns future
                val request = vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME").handler {
                    assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            it.toString(),
                            exception.toString()
                        )
                        context.completeNow()
                    }
                }
                request.end()
                future.fail(exception)
            }
        }

        @Nested
        inner class HTTPGetSchedulesTests {
            @Test
            fun `GET the schedules from a sport that is not registered`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Optional<List<Schedule>>>()
                every { sportsService.async<Optional<List<Schedule>>>(any()) } returns future
                val request =
                    vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME/$SCHEDULES").handler {
                        assertEquals(HTTP_NOT_FOUND, it.statusCode())
                        it.bodyHandler {
                            assert(it.toString() == SPORT_NOT_FOUND)
                            context.completeNow()
                        }
                    }
                request.end()
                future.complete(Optional.empty())
            }

            @Test
            fun `GET the schedules from a sport that is registered but doesnt has any schedule`(
                vertx: Vertx,
                context: VertxTestContext
            ) {
                val future = Future.future<Optional<List<Schedule>>>()
                every { sportsService.async<Optional<List<Schedule>>>(any()) } returns future
                val request =
                    vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME/$SCHEDULES").handler {
                        assertEquals(HTTP_OK, it.statusCode())
                        it.bodyHandler {
                            assert(
                                GSON.fromJson(
                                    it.toJson().toString(),
                                    Array<Schedule>::class.java
                                ).toList() == listOf<Schedule>()
                            )
                            context.completeNow()
                        }
                    }
                request.end()
                future.complete(Optional.of(listOf()))
            }

            @Test
            fun `GET the schedules from a sport that is registered and contains schedules`(
                vertx: Vertx,
                context: VertxTestContext
            ) {
                val future = Future.future<Optional<List<Schedule>>>()
                every { sportsService.async<Optional<List<Schedule>>>(any()) } returns future
                val request =
                    vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME/$SCHEDULES").handler {
                        assertEquals(HTTP_OK, it.statusCode())
                        it.bodyHandler {
                            assert(
                                GSON.fromJson(
                                    it.toJson().toString(),
                                    Array<Schedule>::class.java
                                ).toList() == schedulesForSport1
                            )
                            context.completeNow()
                        }
                    }
                request.end()
                future.complete(Optional.of(schedulesForSport1))
            }

            @Test
            fun `GET the schedules with an internal error`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Optional<List<Schedule>>>()
                every { sportsService.async<Optional<List<Schedule>>>(any()) } returns future
                val request =
                    vertx.createHttpClient().get(SERVER_PORT, LOCAL_HOST, "/$SPORTS/$SPORT_1_NAME/$SCHEDULES").handler {
                        assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                        it.bodyHandler {
                            assertEquals(
                                it.toString(),
                                exception.toString()
                            )
                            context.completeNow()
                        }
                    }
                request.end()
                future.fail(exception)
            }
        }
    }

    @Nested
    inner class HTTPPost {
        @Nested
        inner class HTTPPostASPort {
            @Test
            fun `POST a sport which is not present`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request = vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_RESOURCE_CREATED, it.statusCode())
                    assertEquals(it.getHeader(HTTP_LOCATION_HEADER), "/$SPORTS/${sport1.sportName}")
                    context.completeNow()
                }
                request.end(GSON.toJson(sport1))
                future.complete(Unit)
            }

            @Test
            fun `POST a bad parsed sport`(vertx: Vertx, context: VertxTestContext) {
                val request = vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_WRONG_REQUEST, it.statusCode())
                    context.completeNow()
                }
                request.end("${GSON.toJson(sport1)}extra")
            }

            @Test
            fun `POST a duplicated sport`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request = vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_WRONG_REQUEST, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            it.toString(),
                            DUPLICATED_SPORT
                        )
                        context.completeNow()
                    }
                }
                request.end(GSON.toJson(sport1))
                future.fail(IllegalArgumentException())
            }

            @Test
            fun `POST a sport and an internal error occurs`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request = vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS").handler {
                    assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                    it.bodyHandler {
                        assertEquals(
                            it.toString(),
                            exception.toString()
                        )
                        context.completeNow()
                    }
                }
                request.end(GSON.toJson(sport1))
                future.fail(exception)
            }
        }

        @Nested
        inner class HTTPPostASchedule {
            @Test
            fun `POST a schedule which is duplicated or corresponds to a sport which is not present`(
                vertx: Vertx,
                context: VertxTestContext
            ) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_WRONG_REQUEST, it.statusCode())
                            it.bodyHandler {
                                assertEquals(
                                    it.toString(),
                                    DUPLICATED_SCHEDULE_OR_SPORT_NOT_FOUND
                                )
                                context.completeNow()
                            }
                        }
                request.end(GSON.toJson(schedule1))
                future.fail(IllegalArgumentException())
            }

            @Test
            fun `POST a bad parser schedule`(vertx: Vertx, context: VertxTestContext) {
                val request =
                    vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_WRONG_REQUEST, it.statusCode())
                            context.completeNow()
                        }
                request.end("${GSON.toJson(schedule1)}extra")
            }


            @Test
            fun `POST a schedule`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_RESOURCE_CREATED, it.statusCode())
                            context.completeNow()
                        }
                request.end(GSON.toJson(schedule1))
                future.complete(Unit)
            }

            @Test
            fun `POST a schedule and an internal error occurs`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().post(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                            it.bodyHandler {
                                assertEquals(
                                    it.toString(),
                                    exception.toString()
                                )
                                context.completeNow()
                            }
                        }
                request.end(GSON.toJson(schedule1))
                future.fail(exception)
            }
        }
    }

    @Nested
    inner class HTTPDelete {
        @Nested
        inner class HTTPDeleteAsport {
            @Test
            fun `DELETE a sport which is not present`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME").handler {
                        assertEquals(HTTP_NOT_FOUND, it.statusCode())
                        context.completeNow()
                    }
                request.end()
                future.fail(IllegalArgumentException())
            }

            @Test
            fun `DELETE an existing sport`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME").handler {
                        assertEquals(HTTP_OK, it.statusCode())
                        context.completeNow()
                    }
                request.end()
                future.complete(Unit)
            }

            @Test
            fun `DELETE a sport and an internal error occurs`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request = vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME")
                    .handler {
                        assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                        it.bodyHandler {
                            assertEquals(
                                it.toString(),
                                exception.toString()
                            )
                            context.completeNow()
                        }
                    }
                request.end(GSON.toJson(sport1))
                future.fail(exception)
            }
        }

        @Nested
        inner class HTTPDeleteASchedule {
            @Test
            fun `DELETE a schedule which is not present`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_NOT_FOUND, it.statusCode())
                            it.bodyHandler {
                                assertEquals(
                                    it.toString(),
                                    SPORT_OR_SCHEDULES_NOT_FOUNDS
                                )
                                context.completeNow()
                            }
                        }
                request.end(GSON.toJson(schedule1))
                future.fail(IllegalArgumentException())
            }

            @Test
            fun `DELETE an existing schedule from an existing sport`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_OK, it.statusCode())
                            context.completeNow()
                        }
                request.end(GSON.toJson(schedule1))
                future.complete(Unit)
            }

            @Test
            fun `DELETE a schedule and an internal error occurs`(vertx: Vertx, context: VertxTestContext) {
                val future = Future.future<Unit>()
                every { sportsService.async<Unit>(any()) } returns future
                val request =
                    vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_INTERNAL_ERROR, it.statusCode())
                            it.bodyHandler {
                                assertEquals(
                                    it.toString(),
                                    exception.toString()
                                )
                                context.completeNow()
                            }
                        }
                request.end(GSON.toJson(schedule1))
                future.fail(exception)
            }

            @Test
            fun `DELETE a bad parsed schedule`(vertx: Vertx, context: VertxTestContext) {
                val request =
                    vertx.createHttpClient().delete(SERVER_PORT, LOCAL_HOST, "/$SPORTS/:$SPORT_1_NAME/$SCHEDULES")
                        .handler {
                            assertEquals(HTTP_WRONG_REQUEST, it.statusCode())
                            context.completeNow()
                        }
                request.end("${GSON.toJson(schedule1)}extra")
            }
        }
    }
}

