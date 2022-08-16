package com.mcmouse.nav_tabs.models.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Чтобы создать базу данных sqlite лучше всего воспользоваться классом помощником
 * [SQLiteOpenHelper], конструктор котогого принимает четыре параметра, это контекст, имя
 * базы данных (произвольное типа String), [CursorFactory] (обычно он null) и версию базы данных.
 */
class AppSQLiteHelper(
    private val appContext: Context
) : SQLiteOpenHelper(appContext, "database.db", null, 2) {

    /**
     * логика инициализации базы данных (запросы языка SQL), мы поместили в специальный каталог
     * assets, но можно было это и руками прописать в самом методе. Чтобы получить данные из
     * каталога, нужно у контекста вызвать свойство assets и далее метод open, таким образом мы
     * получим обычный InputStream. Для удобства работы с даннымы этого типа воспользуемся
     * преимуществами котлина, а именно вызовем extension метод [bufferedReader()], у которого
     * вызовем функцию [use] - аналог try/catch with resources в java, то есть по завершению работы
     * он автоматически закроет поток. Так как в каталоге находятся сразу все запросы, одновременно
     * выполнить их не возможно, поэтому разобъем их по уникальному разделителю внутри файла, а
     * именно по точке с запятой. Чтобы выполнить запрос SQL на создание таблицы, нужно
     * у объекта базы данных [SQLiteDatabase] вызвать метод [execSQL()], в который в качестве
     * параметра нужно передать строку с содержанием запроса.
     */
    override fun onCreate(db: SQLiteDatabase) {
        val sql = appContext.assets.open("db_init.sql").bufferedReader().use {
            it.readText()
        }
        sql.split(';')
            .filter { it.isNotBlank() }
            .forEach { db.execSQL(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}