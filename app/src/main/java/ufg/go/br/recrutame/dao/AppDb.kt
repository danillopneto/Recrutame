package ufg.go.br.recrutame.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import rec.protelas.User
import ufg.go.br.recrutame.dao.UserDao

/**
 * Created by Vinicius on 15/07/2018.
 */
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
}