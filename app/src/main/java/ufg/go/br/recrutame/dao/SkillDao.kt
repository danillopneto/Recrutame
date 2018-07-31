package ufg.go.br.recrutame.dao

import android.arch.persistence.room.*
import ufg.go.br.recrutame.model.Skill
import rec.protelas.User
import android.arch.persistence.room.Delete






/**
 * Created by Vinicius on 28/07/2018.
 */
@Dao
interface SkillDao {

    @Query("SELECT * FROM Skill WHERE email LIKE :email")
    abstract fun all(email: String): List<Skill>

    @Query("SELECT * FROM Skill")
    fun loadAllSkills(): Array<Skill>

    @Query("SELECT skills FROM Skill WHERE Skill.email LIKE :email")
    fun getSkillEmail(email: String): Skill

    @Query("SELECT count(*) FROM Skill")
    fun getNumberOfRows(): Int

    @Query("SELECT * FROM Skill WHERE Skill.email LIKE :email AND Skill.skills LIKE :skill")
    fun getSkillReplace(email: String, skill: String): Skill

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg skill: Skill)

   // @Insert
  //  fun add(vararg skill: Skill)

    @Update
    fun update(vararg skill: Skill)


    @Delete
    fun deleteUsers(vararg skills: Skill)

    @Delete
    fun deleteAll(skill1: Skill , skill2: Skill)

    @Delete
    fun deleteWithFriends(skill: Skill , friends: List<Skill>)

}