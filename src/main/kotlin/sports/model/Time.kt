package sports.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
private const val NONE = -1
const val ANY_HOUR: Int = 23
const val ANY_MIN: Int = 59

/**
 * A class representing a time (hours and minutes)
 *
 */

class Time {
    /**
     * an [Int] representing the hour
     */
    @BsonProperty("hour")
    var hour: Int
    /**
     * an [Int] representing the minutes
     */
    @BsonProperty("min")
    var min: Int

    @BsonCreator constructor(){
        hour=NONE
        min=NONE
    }

    constructor(hour: Int, min: Int) {
        this.hour = hour
        this.min = min
    }

    /**
     * structural equals() implementation
     *
     * @param other [Time] to compare against
     * @return true if they are equals, false otherwise
     */
    override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Time

            if (hour != other.hour) return false
            if (min != other.min) return false

            return true
        }

    /**
     * structural hashcode() implementation
     *
     * @return an [Int] with the hashcode
     */
        override fun hashCode(): Int {
            var result = hour
            result = 31 * result + min
            return result
        }

    }
