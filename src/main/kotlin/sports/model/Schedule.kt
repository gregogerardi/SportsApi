package sports.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

/**
 * A class representing a schedule
 *
 */

class Schedule {

    /**
     * a [Time] to indicate the start of the [Schedule]
     */
    @BsonProperty("fromHour")
    var fromHour: Time

    /**
     * a [Time] to indicate the end of the [Schedule]
     */
    @BsonProperty("toHour")
    var toHour: Time

    /**
     * a [Time] to indicate the day of the week
     */
    @BsonProperty("day")
    var day: Day


    @BsonCreator
    constructor () {
    fromHour = Time()
        toHour=Time()
        day=Day.NONE
    }

    constructor(fromHour: Time, toHour: Time, day: Day) {
        this.fromHour = fromHour
        this.toHour = toHour
        this.day = day
    }

    /**
     * structural equals() implementation
     *
     * @param other [Schedule] to compare against
     * @return true if they are equals, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schedule

        if (fromHour != other.fromHour) return false
        if (toHour != other.toHour) return false
        if (day != other.day) return false

        return true
    }

    /**
     * structural hashcode() implementation
     *
     * @return an [Int] with the hashcode
     */
    override fun hashCode(): Int {
        var result = fromHour.hashCode()
        result = 31 * result + toHour.hashCode()
        result = 31 * result + day.hashCode()
        return result
    }
}
