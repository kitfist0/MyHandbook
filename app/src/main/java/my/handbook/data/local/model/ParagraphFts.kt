package my.handbook.data.local.model

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Paragraph::class)
@Entity(tableName = "paragraphs_fts")
data class ParagraphFts(
    val text: String,
)
