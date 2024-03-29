package my.handbook.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paragraphs")
data class Paragraph(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val file: String,
    val section: Int,
    val text: String,
)
