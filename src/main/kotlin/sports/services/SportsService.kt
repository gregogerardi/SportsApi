package sports.services

import io.vertx.core.Future
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sports.model.Schedule
import sports.model.Sport
import sports.model.repository.SportsRepository

class SportsService(private val sportsRepository: SportsRepository) {

    /**
     * Wrapper function to run a synchronously long or blocking [task] into a coroutine and return the result as a [Future]
     * If the task completes without any exception, the result will be into the future and any handler will be called. If an [Exception]
     * occurs the future will fail
     *
     * @param T
     * @param task
     * @return
     */
    fun <T> async(task: suspend SportsService.() -> T): Future<T> {
        val future = Future.future<T>()
        GlobalScope.launch {
            try {
                future.complete(task())
            } catch (e: Exception) {
                future.fail(e)
            }
        }
        return future
    }

    /**
     * Searches for every [Sport]
     *
     * @return a [List] containing all the [Sport]s or an empty [List] if none is available
     */
    fun getAll() = sportsRepository.getAll()

    /**
     * Searches for a single[Sport]
     *
     * @param sportName is the name of the sport to search for
     * @return an [Optional] containing a [Sport] if anyone match its[sportName] or an empty one otherwise
     */
    fun getSport(sportName: String) = sportsRepository.getSport(sportName)

    /**
     * Searches for the [Schedule]s [List] of a given [Sport]
     *
     * @param sportName is the name of the sport to search the schedules for
     * @return a [Optional] containing the schedules [List] for the give [Sport]. If the [Sport] is not present into de
     * repository will be an empty [Optional]. If the [Sport] exists but does not has any scheduled [Schedule] will return
     * an [Optional] with an empty [List]
     */
    fun getSchedule(sportName: String) = sportsRepository.getSchedules(sportName)

    /**
     * Add a new [Sport]
     * if the [Sport] is already registered will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sport is the [Sport] to be added
     */
    fun addSport(sport: Sport) = sportsRepository.addSport(sport)

    /**
     * Add a new [Schedule] into a [Sport]
     * if the [Sport] is not registered or the [schedule] already exists will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName is the [Sport]'s name to add the schedule.
     * @param schedule is the [Schedule] to be added to the asked sport's schedules
     */
    fun addSchedule(sportName: String, schedule: Schedule) = sportsRepository.addSchedule(sportName, schedule)

    /**
     * Searches for a [Sport] that matches the given [sportName] and removes them
     * if the [sportName] does not match to any [Sport] will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName to search for the [Sport] that matches
     */
    fun removeSport(sportName: String) = sportsRepository.removeSport(sportName)

    /**
     * Searches for a [Sport] than matches the given [sportName] and removes the given [schedule] from its schedules
     * if the [sportName] does not match to any [Sport] or the matched one has not [schedule] into its schedules will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName to search for
     * @param schedule to remove from the [Sport]
     */
    fun removeSchedule(sportName: String, schedule: Schedule) = sportsRepository.removeSchedule(sportName, schedule)
}