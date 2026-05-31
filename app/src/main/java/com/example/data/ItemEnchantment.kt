package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "item_enchantments")
data class ItemEnchantment(
    @PrimaryKey val itemId: String,
    val checkedEnchantments: String // Comma-separated active enchantments list
)

@Dao
interface ItemEnchantmentDao {
    @Query("SELECT * FROM item_enchantments")
    fun getAll(): Flow<List<ItemEnchantment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEnchantment)

    @Query("SELECT * FROM item_enchantments WHERE itemId = :itemId")
    suspend fun getById(itemId: String): ItemEnchantment?
}

@Database(entities = [ItemEnchantment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): ItemEnchantmentDao
}

class EnchantmentRepository(private val dao: ItemEnchantmentDao) {
    val allEnchantments: Flow<List<ItemEnchantment>> = dao.getAll()

    suspend fun saveItemEnchantments(itemId: String, list: List<String>) {
        val commaSeparated = list.joinToString(",")
        dao.insert(ItemEnchantment(itemId, commaSeparated))
    }
}
