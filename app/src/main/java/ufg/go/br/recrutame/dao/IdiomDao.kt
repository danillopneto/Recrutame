package ufg.go.br.recrutame.dao

import android.arch.persistence.room.*
import ufg.go.br.recrutame.model.Idiom

/**
 * Created by Vinicius on 29/07/2018.
 */
@Dao
interface IdiomDao {

    @Query("SELECT * FROM Idiom WHERE email LIKE :email")
    abstract fun all(email: String): List<Idiom>

    @Query("SELECT * FROM Idiom")
    fun loadAllIdioms(): Array<Idiom>

    @Query("SELECT Idioms FROM Idiom WHERE Idiom.email LIKE :email")
    fun getIdiomEmail(email: String): Idiom

    @Query("SELECT count(*) FROM Idiom")
    fun getNumberOfRows(): Int

    @Query("SELECT * FROM Idiom WHERE Idiom.email LIKE :email AND Idiom.Idioms LIKE :idiom")
    fun getIdiomReplace(email: String, idiom: String): Idiom

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg idioms: Idiom)

    @Update
    fun update(vararg idiom: Idiom)

    @Delete
    fun deleteUsers(vararg idiom: Idiom)

    @Delete
    fun deleteAll(idioms1: Idiom , idioms2: Idiom)

    @Delete
    fun deleteWithFriends(idiom: Idiom , friends: List<Idiom>)

}