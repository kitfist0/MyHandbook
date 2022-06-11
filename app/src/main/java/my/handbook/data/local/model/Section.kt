package my.handbook.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey
    val id: Int,
    val selected: Boolean,
)
