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

    @Query("SELECT * FROM User WHERE user.email LIKE :email")
    fun getUserEmail(email: String): User

    @Query("SELECT * FROM User WHERE email LIKE '%' || :email  || '%'")
    fun getByEmail(email: String): User

    @Query("SELECT * FROM User WHERE id = :id")
    fun getById(id: Int): User

    @Query("SELECT id FROM User WHERE user.email LIKE :email")
    fun getReturnID(email: String): User

    @Query("SELECT count(*) FROM User WHERE user.email LIKE :email")
    fun getNumberOfRowsEmail(email: String): Int

    @Query("SELECT count(*) FROM User")
    fun getNumberOfRows(): Int

    @Update
    fun update(vararg user: User)

    @Insert
    fun add(vararg user: User)

    @Delete
    fun delete(vararg user: User)

}