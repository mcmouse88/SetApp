package com.mcmouse88.j_unit

class TestErrorHandler : ErrorHandler<String> {

    private val _records = mutableListOf<Record>()
    val records: List<Record> get() = _records
    val invokeCount: Int get() = records.size

    override fun onError(exception: Exception, resource: String) {
        _records.add(Record(exception, resource))
    }

    data class Record(
        val exception: Exception,
        val resource: String
    )
}