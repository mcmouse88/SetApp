package com.mcmouse88.multi_choice_list.presentation.list.adapter

import com.mcmouse88.multi_choice_list.presentation.list.CatListItem

interface CatsAdapterListener {
    fun onCatDelete(cat: CatListItem)
    fun onCatToggleFavorite(cat: CatListItem)
    fun onCatChosen(cat: CatListItem)
    fun onCatToggle(cat: CatListItem)
}