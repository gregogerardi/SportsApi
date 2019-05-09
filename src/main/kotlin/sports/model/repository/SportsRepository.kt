package sports.model.repository

import sports.model.Schedule
import sports.model.Sport
import java.util.*


/**
 * An interface for repository of [Sport]s
 * every access to the repository should be treated as a synchronously one
 */
interface SportsRepository {


    /**
     * Searches the repository for every [Sport]
     *
     * @return a [List] containing all the [Sport]s or an empty [List] if none is available
     */
    fun getAll(): List<Sport>

    /**
     * Searches the repository for a single[Sport]
     *
     * @param sportName is the name of the sport to search for
     * @return an [Optional] containing a [Sport] if anyone match its[sportName] or an empty one otherwise
     */
    fun getSport(sportName: String): Optional<Sport>

    /**
     * Searches the repository for the [Schedule]s [List] of a given [Sport]
     *
     * @param sportName is the name of the sport to search the schedules for
     * @return a [Optional] containing the schedules [List] for the give [Sport]. If the [Sport] is not present into de
     * repository will be an empty [Optional]. If the [Sport] exists but does not has any scheduled [Schedule] will return
     * an [Optional] with an empty [List]
     */
    fun getSchedules(sportName: String): Optional<List<Schedule>>

    /**
     * Add a new [Sport] into the repository.
     * if the [Sport] is already into the repository will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sport is the [Sport] to be added to the repository
     */
    fun addSport(sport: Sport)

    /**
     * Add a new [Schedule] into a [Sport] from the repository.
     * if the [Sport] is not into the repository or the Schedule exists will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName is the [Sport]'s name to search it into the repository.
     * @param schedule is the [Schedule] to be added to the asked sport's schedules
     */
    fun addSchedule(sportName: String, schedule: Schedule)

    /**
     * Searches for a [Sport] that matches the given [sportName] and removes them from the repository.
     * if the [sportName] does not match to any [Sport] will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName to search for the [Sport] that matchs
     */
    fun removeSport(sportName: String)

    /**
     * Searches for a [Sport] than matches the given [sportName] and removes the given [Schedule] [schedule] from its schedules
     * if the [sportName] does not match to any [Sport] or the matched one has not [schedule] into its schedules will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName to search for
     * @param schedule to remove from the [Sport]
     */
    fun removeSchedule(sportName: String, schedule: Schedule)
}
