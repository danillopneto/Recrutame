package ufg.go.br.recrutame.dao

import android.arch.persistence.room.*
import ufg.go.br.recrutame.model.Skill


/**
 * Created by Vinicius on 28/07/2018.
 */
@Dao
interface SkillDao {

    @Query("SELECT * FROM Skill")
    fun all(): List<Skill>

    @Query("SELECT * FROM Skill")
    fun loadAllUsers(): Array<Skill>

    @Query("SELECT skills FROM Skill WHERE Skill.email LIKE :email")
    fun getSkillEmail(email: String): Skill


}