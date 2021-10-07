package my.handbook.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val file: String,
    val section: Int,
    val summary: String,
    var favorite: Boolean = false,
)
