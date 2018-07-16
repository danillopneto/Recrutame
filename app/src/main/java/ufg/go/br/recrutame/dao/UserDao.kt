package ufg.go.br.recrutame.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import rec.protelas.User
import android.arch.persistence.room.Update






/**
 * Created by Vinicius on 15/07/2018.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun all(): List<User>

    @Query("SELECT * FROM User")
    fun loadAllUsers(): Array<User>

    @Query("SELECT * FROM User WHERE user.nome LIKE :userName")
    fun findUserEmail(userName: String): List<User>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getById(id: Int): User

    @Update
    fun update(vararg user: User)

    @Insert
    fun add(vararg user: User)

    @Delete
    fun delete(vararg user: User)

}