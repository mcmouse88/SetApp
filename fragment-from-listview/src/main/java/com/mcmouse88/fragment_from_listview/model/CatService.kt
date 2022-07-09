package com.mcmouse88.fragment_from_listview.model

import com.github.javafaker.Faker

class CatService {
    val cats: List<Cat> = (1..30).map { Cat(
        id = it,
        name = Faker.instance().cat().name(),
        description = Faker.instance().lorem().paragraph(10)
    ) }
}