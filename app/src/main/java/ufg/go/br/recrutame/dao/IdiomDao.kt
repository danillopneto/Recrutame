package ufg.go.br.recrutame.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ufg.go.br.recrutame.model.Skill

/**
 * Created by Vinicius on 29/07/2018.
 */
@Dao
interface IdiomDao {

    @Query("SELECT * FROM Idiom")
    fun all(): List<Skill>

    @Query("SELECT * FROM Idiom")
    fun loadAllUsers(): Array<Skill>

    @Query("SELECT skills FROM Idiom WHERE Idiom.email LIKE :email")
    fun getIdiomEmail(email: String): Skill


}