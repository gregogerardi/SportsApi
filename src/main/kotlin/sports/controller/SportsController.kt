package sports.controller

import io.vertx.ext.web.Router
import sports.model.Schedule
import sports.model.Sport
import sports.services.SportsService

//strings
const val SPORT_NAME = "sportName"
const val SPORTS = "sports"
const val SCHEDULES = "schedules"

//error messages
const val DUPLICATED_SPORT = "Sport already registered"
const val SPORT_NOT_FOUND = "the specified sport is not registered"
private const val SCHEDULE_NOT_FOUND = "the specified schedule is not registered"
private const val DUPLICATED_SCHEDULE = "the specified schedule is already registered"

//http headers names
const val HTTP_LOCATION_HEADER = "LOCATION"

//http response status codes
const val HTTP_OK = 200
const val HTTP_NOT_FOUND = 404
const val HTTP_INTERNAL_ERROR = 500
const val HTTP_RESOURCE_CREATED = 201
const val HTTP_WRONG_REQUEST = 400

const val DUPLICATED_SCHEDULE_OR_SPORT_NOT_FOUND = "$DUPLICATED_SCHEDULE or $SPORT_NOT_FOUND"

const val SPORT_OR_SCHEDULES_NOT_FOUNDS = "$SPORT_NOT_FOUND or $SCHEDULE_NOT_FOUND"

class SportsController {
    companion object {
        fun initRouter(router: Router, sportsService: SportsService): Router {

            //actions related to sports
            router.get("/$SPORTS/:$SPORT_NAME").handler {
                val sportName = it.param(SPORT_NAME)
                if (sportName.isPresent) it.handleFuture(
                    sportsService.async { getSport(sportName.get()) },
                    {
                        if (it.isPresent) response().setStatusCode(HTTP_OK).end(GSON.toJson(it.get()))
                        else response().setStatusCode(HTTP_NOT_FOUND).end()
                    },
                    { response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString()) })
            }

            router.get("/$SPORTS").handler {
                it.handleFuture(
                    sportsService.async { getAll() },
                    { response().setStatusCode(HTTP_OK).end(GSON.toJson(it)) },
                    { response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString()) })
            }

            router.post("/$SPORTS").awaitBody().handler {
                val sport = it.bodyAs<Sport>(Sport::class)
                if (sport.isPresent) {
                    it.handleFuture(
                        sportsService.async { addSport(sport.get()) },
                        {
                            response().setStatusCode(HTTP_RESOURCE_CREATED).putHeader(
                                HTTP_LOCATION_HEADER,
                                "/$SPORTS/${sport.get().sportName}"
                            ).end()
                        }, {
                            if (it is IllegalArgumentException) response().setStatusCode(HTTP_WRONG_REQUEST).end(
                                DUPLICATED_SPORT
                            )
                            else response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString())
                        })
                }
            }

            router.delete("/$SPORTS/:$SPORT_NAME").handler {
                val sportName = it.param(SPORT_NAME)
                if (sportName.isPresent) {
                    it.handleFuture(
                        sportsService.async { removeSport(sportName.get()) },
                        { response().setStatusCode(HTTP_OK).end() },
                        {
                            if (it is IllegalArgumentException) response().setStatusCode(HTTP_NOT_FOUND).end()
                            else response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString())
                        })
                }
            }


            //actions related to sports's schedule

            router.get("/$SPORTS/:$SPORT_NAME/$SCHEDULES").handler {
                val sportName = it.param(SPORT_NAME)
                if (sportName.isPresent) {
                    it.handleFuture(sportsService.async { getSchedule(sportName.get()) }, {
                        if (it.isPresent) response().setStatusCode(HTTP_OK).end(GSON.toJson(it.get()))
                        else response().setStatusCode(HTTP_NOT_FOUND).end(SPORT_NOT_FOUND)
                    }, { response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString()) })
                }
            }

            router.post("/$SPORTS/:$SPORT_NAME/$SCHEDULES").awaitBody().handler {
                val sportName = it.param(SPORT_NAME)
                if (sportName.isPresent) {
                    val schedule = it.bodyAs<Schedule>(Schedule::class)
                    it.handleFuture(
                        sportsService.async { addSchedule(sportName.get(), schedule.get()) },
                        { response().setStatusCode(HTTP_RESOURCE_CREATED).end() },
                        {
                            if (it is IllegalArgumentException) response().setStatusCode(HTTP_WRONG_REQUEST).end(
                                DUPLICATED_SCHEDULE_OR_SPORT_NOT_FOUND
                            )
                            else response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString())
                        })
                }
            }

            router.delete("/$SPORTS/:$SPORT_NAME/$SCHEDULES").awaitBody().handler {
                val sportName = it.param(SPORT_NAME)
                if (sportName.isPresent) {
                    val schedule = it.bodyAs<Schedule>(Schedule::class)
                    if (schedule.isPresent) {
                        it.handleFuture(
                            sportsService.async { removeSchedule(sportName.get(), schedule.get()) },
                            { response().setStatusCode(HTTP_OK).end() },
                            {
                                if (it is IllegalArgumentException) response().setStatusCode(HTTP_NOT_FOUND).end(
                                    SPORT_OR_SCHEDULES_NOT_FOUNDS
                                )
                                else response().setStatusCode(HTTP_INTERNAL_ERROR).end(it.toString())
                            })
                    }
                }
            }
            return router
        }
    }
}


