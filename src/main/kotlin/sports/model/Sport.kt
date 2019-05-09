package sports.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

/**
 * A class representing a sport
 *
 */

class Sport {

    /**
     * the name of the sport
     */
    @BsonId
    var sportName: String

    /**
     * a list of [Schedule]s for this sport
     */
    @BsonProperty
    var schedules : List<Schedule>

    @BsonCreator constructor(){
        sportName= ""
        schedules = listOf()
    }

    constructor(sportName: String, schedules: List<Schedule>) {
        this.sportName = sportName
        this.schedules = schedules
    }

    /**
     * an equals implementation to compare two [Sport]s using only the [sportName] field
     *
     * @param other [Sport] to compare against
     * @return true if the sports are equal according to their names
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sport

        if (sportName != other.sportName) return false

        return true
    }

    /**
     * a hashcode based only in the [sportName] of this [Sport]
     *
     * @return the hashcode as an [Int]
     */
    override fun hashCode(): Int {
        return sportName.hashCode()
    }


}