package com.mcmouse88.remote_mediator.data.retrofit

import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class LaunchesQuery(
    val query: QueryFilter?,
    val options: QueryOptions
) {
    companion object {
        fun create(year: Int?, offset: Int, limit: Int): LaunchesQuery {
            val query = year?.let {
                QueryFilter(dataUnix = yearToUnixTimestampRange(it))
            }
            return LaunchesQuery(
                query = query,
                options = QueryOptions(
                    offset = offset,
                    limit = limit
                )
            )
        }

        private fun yearToUnixTimestampRange(year: Int): QueryRange {
            val calendar = Calendar.getInstance()
            calendar.setYear(year)
            val start = calendar.getUnixTimeStamp()
            calendar.setYear(year + 1)
            val end = calendar.getUnixTimeStamp()
            return QueryRange(
                greaterOrEqualsThan = start,
                lessThen = end
            )
        }

        private fun Calendar.setYear(year: Int) {
            set(year, Calendar.JANUARY, 1, 0, 0, 0)
        }

        private fun Calendar.getUnixTimeStamp(): Long {
            return TimeUnit.MILLISECONDS.toSeconds(timeInMillis)
        }
    }
}

data class QueryOptions(
    val offset: Int,
    val limit: Int,
    val sort: String = "-date_unix",
    val select: String = "flight_number name details links date_unix success"
)

data class QueryFilter(
    val dataUnix: QueryRange
)

data class QueryRange(
    @SerializedName("\$gte") val greaterOrEqualsThan: Long,
    @SerializedName("\$lt") val lessThen: Long
)