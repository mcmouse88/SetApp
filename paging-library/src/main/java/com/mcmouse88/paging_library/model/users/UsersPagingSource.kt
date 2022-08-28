package com.mcmouse88.paging_library.model.users

import androidx.paging.PagingSource
import androidx.paging.PagingState

typealias UsersPageLoader = suspend (pageIndex: Int, pageSize: Int) -> List<User>


/**
 * [PagingSource] класс от которого нужно отнаследоваться, и реализовать два метода, первый, для
 * загрузки данных ([load()], а второй для вычисления аргументов пагинации в случае если текущее
 * состояние списка стало невалидным, например пользователь запросил обновление данных с нуля. При
 * создании данного класса нужно указать с какими данными мы работаем ([User]), и какие аргументы
 * пагинации мы используем. Как правило в качестве аргумента пагинации используется тип Int, и
 * может представлять собой индекс элементов, с которого нужно начинать загрузка, либо индекс
 * страницы, с которой нужно начинать загрузку. Что именно он будет из себя представлять решает
 * кодер.
 */
class UsersPagingSource(
    private val loader: UsersPageLoader
) : PagingSource<Int, User>() {

    /**
     * Данный метод используется в тех случаях, когда источник данных инвалидируется (например
     * в базе данных обновились данные), и нужно перезагрузить данные. В данном методе нам нужно
     * указать, что мы будем загружать в первую очередь. Этот метод должен вернуть ключ (индекс)
     * с которого начнется загрузка данных после того как текущие данные стали невалдиными.
     * С этим методом можно в целом и не заморачиваться, а просто вернуть ноль (тогда данные
     * загрузаться с первой страницы), но мы же вычислем, какой индекс был последним, и попытаемся
     * загрузить данные с него, в таком случае пагинация также появится и при перелистывании на
     * предыдущую страницу. Так как у нас нет метода получения текущего индекса, но есть предыдущий
     * и следующий, то мы попробуем прибавить единицу к предыдущему индексу, и если не получится
     * то тогда от последующего отнимем едининцу.
     */
    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.nextKey?.minus(1) ?: page.prevKey?.plus(1)
    }

    /**
     * Данный метод является suspend, а значит в нем можно безопасно инициировать загрузку данных.
     * В данном мметоде нужно прочитать параметры, которые приходят в классе [LoadParams], из
     * него достать аргументы пагинации, как-то их обработать, после вызвать метод для загрузки
     * данных (в нашем случае это typealias, но можно это было сделать и напрямую через
     * объект [UsersDao]. Если загрузка прошла успешно, то мы должны вернуть тип [LoadResult.Page].
     * Если в процессе загрузки данных произошла ошибка, то мы возвращаем тип [LoadResult.Error].
     * Изначально мы берем ключ, который есть у нас в парасметрах, это и есть наш элемент
     * пагинации. В данном случае мы его интерпретируем как индекс страницы. Если он равен null,
     * то значит это первая загрузка данных, в связи с чем индекс будет равен 0. Второй параметр,
     * который нам нужен для загрузки данных, это [loadsize], он может отличаться для первой
     * загрузки, и для последующих, по умолчанию первая страница загружается в количестве элементов
     * помноженном на три, то есть, если [loadsize] будет равен 10, то первая загрузка загрузит
     * 30 элементов, а последующие загрузки уже будут загружать по 10 элементов. В [LoadResult.Page]
     * нам нужно передать следующие параметры, это данные загруженные из источника (в нашем случае
     * список пользователей), далее нам нужно указать какой элемент пагинации должен быть предыдущим,
     * и какой следующим. prevKey мы проверяем, если индекс равен нулю, значит предыдущего индекса
     * нет, поэтому возвращаем null, иначе отнимаем единицу у текущего индекса.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val pageIndex = params.key ?: 0

        return try {
            val users = loader.invoke(pageIndex, params.loadSize)
            return LoadResult.Page(
                data = users,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = if (users.size == params.loadSize) pageIndex + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(
                throwable = e
            )
        }
    }
}