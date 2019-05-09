package sports.model.repository

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Updates
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import sports.model.Schedule
import sports.model.Sport
import java.util.*

private const val SCHEDULES = "schedules"

/**
 * An implementation of [SportsRepository] that connects to a [MongoCollection] of [Sport]s
 *
 * @property collection is the [MongoCollection] to connect with for queries
 */

class SportsMongoDB(private val collection: MongoCollection<Sport>) : SportsRepository {

    /**
     * Searches the database for every [Sport] contained.
     *
     * @return a [List] of [Sport]s. If none the database is empty returns an empty [List]
     */
    override fun getAll() = collection.find().toList()

    /**
     * Searches the database for a single[Sport]
     *
     * @param sportName is the name of the sport to search for
     * @return an [Optional] containing a [Sport] if anyone match its[sportName] or an empty one otherwise
     */
    override fun getSport(sportName: String) = Optional.ofNullable(collection.findOne { Sport::sportName eq sportName })

    /**
     * Searches the database for the [Schedule]s [List] of a given [Sport]
     *
     * @param sportName is the name of the sport to search the schedules for
     * @return a [Optional] containing the schedules [List] for the given [Sport]. If the [Sport] is not present into de
     * repository will be an empty [Optional]. If the [Sport] exists but does not has any scheduled [Schedule] will return
     * an [Optional] with an empty [List]
     */
    override fun getSchedules(sportName: String): Optional<List<Schedule>> {
        val optionalSport = Optional.ofNullable((collection.findOne { Sport::sportName eq sportName }))
        return if (!optionalSport.isPresent) Optional.empty() else Optional.ofNullable(optionalSport.get().schedules)
    }

    /**
     * Add a new [Sport] into the database.
     * if the [Sport] is already into the repository will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sport is the [Sport] to be added to the repository
     */
    override fun addSport(sport: Sport) = try {
        collection.insertOne(sport)
    } catch (e: MongoWriteException) { //"duplicated key" throw this exception in MongoDB
        throw IllegalArgumentException(e)
    }

    /**
     * Add a new [Schedule] into a [Sport] from the database.
     * if the [Sport] is not into the database or the Schedule already exists will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName is the [Sport]'s name to search for into the repository.
     * @param schedule is the [Schedule] to be added to the [Sport]'s schedules
     */
    override fun addSchedule(sportName: String, schedule: Schedule) {
        val optionalSport = getSport(sportName)
        if (!optionalSport.isPresent || optionalSport.get().schedules.contains(schedule))
            throw IllegalArgumentException()
        else collection.findOneAndUpdate(Sport::sportName eq sportName, Updates.addToSet(SCHEDULES, schedule))
    }

    /**
     * Searches for a [Sport] that matches the given [sportName] and removes them from the database.
     * if the [sportName] does not match to any [Sport] will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName to search for the [Sport] that matches
     */
    override fun removeSport(sportName: String) {
        val optionalSport = Optional.ofNullable(collection.findOneAndDelete(Sport::sportName eq sportName))
        if (!optionalSport.isPresent) throw IllegalArgumentException()
    }

    /**
     * Searches for a [Sport] than matches the given [sportName] and removes the given [Schedule] from its schedules
     * if the [sportName] does not match to any [Sport] or the matched one has not [schedule] into its schedules will throw an [IllegalArgumentException]
     *
     * @throws [IllegalArgumentException]
     * @param sportName to search for
     * @param schedule to remove from the [Sport]
     */
    override fun removeSchedule(sportName: String, schedule: Schedule) {

        val optionalSport = getSport(sportName)
        if (!optionalSport.isPresent || !optionalSport.get().schedules.contains(schedule))
            throw IllegalArgumentException()
        else collection.findOneAndUpdate(
            Sport::sportName eq sportName,
            Updates.pull(SCHEDULES, schedule)
        )
    }
}