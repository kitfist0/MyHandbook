package my.handbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey
    val id: Int,
    var selected: Boolean,
)
