package ufg.go.br.recrutame.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ufg.go.br.recrutame.model.Skill

/**
 * Created by Vinicius on 28/07/2018.
 */
@Database(entities = [Skill::class], version = 1, exportSchema = false)
abstract class AppDbSkill : RoomDatabase() {
    abstract fun skillDao(): SkillDao
}