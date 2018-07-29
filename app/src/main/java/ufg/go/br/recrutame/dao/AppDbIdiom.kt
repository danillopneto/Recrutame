package ufg.go.br.recrutame.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ufg.go.br.recrutame.model.Idiom

/**
 * Created by Vinicius on 29/07/2018.
 */
@Database(entities = [Idiom::class], version = 1, exportSchema = false)
abstract class AppDbIdiom : RoomDatabase() {
    abstract fun idiomDao(): IdiomDao
}