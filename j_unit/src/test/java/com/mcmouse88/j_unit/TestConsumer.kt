package com.mcmouse88.j_unit

class TestConsumer : Consumer<String> {

    private val _resources = mutableListOf<String>()
    val resources: List<String> get() = _resources

    val lastResource: String? get() = resources.lastOrNull()
    val invokeCount: Int get() = resources.size

    override fun invoke(resource: String) {
        _resources.add(resource)
    }
}