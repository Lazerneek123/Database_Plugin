package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.Column
import com.example.demo.model.ColumnAdvancedSettings
import com.example.demo.model.EntityAdvancedSettings
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.TableCreate
import javax.swing.DefaultListModel

class GenCreateTable(
    private val entityAttribute: EntityAdvancedSettings?,
    private val isEntity: Boolean,
    private val isColumnInfo: Boolean
) {
    private var code = ""

    fun generate(
        config: TableCreate, listModelColumnsAttribute: DefaultListModel<ColumnAdvancedSettings?>
    ): String {
        code = """
                package ${config.getPath()}

                import androidx.room.Entity
                import androidx.room.PrimaryKey
                ${if (isColumnInfo) "import androidx.room.ColumnInfo" else ""}

                ${generateEntity()}
                data class ${CapitalizeFirstLetter().uppercaseChar(config.getTableName())}(
                ${generateColumns(config.getListColumnData(), listModelColumnsAttribute)}
                ) {
                    ${generatePrimaryKeys(config.getListPrimaryKeyData())}       
                }
            """.trimIndent()

        return code
    }

    private fun generateEntity(): String {
        var content = ""

        if (isEntity) {
            content = """@Entity(${
                if (entityAttribute!!.tableName != null)
                    "\n                        tableName = \"${entityAttribute.tableName}\","
                else ""
            }${
                if (entityAttribute.primaryKeys != null)
                    "\n                        primaryKeys = [${getPrimaryKeys()}" +
                            "],"
                else ""
            }
            foreignKeys = [
                ForeignKey(
                    entity = User::class,
                    parentColumns = ["id"],
                    childColumns = ["authorId"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE,
                    deferred = false
                )
            ],
            indices = [
                Index(
                    name = "index_author_and_title",
                    value = ["authorId"],
                    orders = [Index.Order.ASC],
                    unique = true
                ),
                Index(value = ["title"])
            ],
            ${
                if (entityAttribute.inheritSuperIndices != null)
                    "inheritSuperIndices = ${entityAttribute.inheritSuperIndices},"
                else ""
            }
            ignoredColumns = ["temporaryData"]
        )"""
        } else {
            content = """@Entity"""
        }

        //content = """@Entity"""

        //content = """@Entity = $isEntity / isColumnInfo = $isColumnInfo"""

        return content
    }

    private fun getPrimaryKeys(): String {
        var content = ""

        entityAttribute!!.primaryKeys!!.forEach { element ->
            val cont = """    
                    "$element","""
            content += cont

        }

        return content
    }

    private fun generatePrimaryKeys(
        list: List<PrimaryKey>
    ): String {
        var content = ""

        list.forEachIndexed { index, item ->
            if (index >= 1) {
                content += "\n\n"
            }
            val autoGenerate = item.autoGenerateValue
            val name = item.name
            val dataType = item.dataType
            val value = item.value

            val cont = """
                    @PrimaryKey(autoGenerate = $autoGenerate)
                    var $name: $dataType = $value"""
            content += cont
        }
        return content
    }

    private fun generateColumns(
        list: List<Column>, listModelColumnsAttribute: DefaultListModel<ColumnAdvancedSettings?>
    ): String {
        var content = ""

        if (isColumnInfo) {
            for (i in list.indices) {
                if (i >= 1) {
                    content += """
                    
                """
                }
                val nullable = list[i].nullable
                val cont = """    @ColumnInfo(
                        name = "${listModelColumnsAttribute[i]!!.name}",${
                    if (listModelColumnsAttribute[i]!!.typeAffinity != null) {
                        "\n                        typeAffinity = ColumnInfo.${listModelColumnsAttribute[i]!!.typeAffinity},"
                    } else {
                        ""
                    }
                }${
                    if (listModelColumnsAttribute[i]!!.index != null) {
                        "\n                        index = ${listModelColumnsAttribute[i]!!.index},"
                    } else {
                        ""
                    }
                }${
                    if (listModelColumnsAttribute[i]!!.defaultValue != null) {
                        "\n                        defaultValue = \"${listModelColumnsAttribute[i]!!.defaultValue}\","
                    } else {
                        ""
                    }
                }${
                    if (listModelColumnsAttribute[i]!!.collate != null) {
                        "\n                        collate = ColumnInfo.${listModelColumnsAttribute[i]!!.collate}"
                    } else {
                        ""
                    }
                }
                    )
                    val ${list[i].name}: ${list[i].dataType}${columnQuestionMark(nullable)} = ${list[i].value}"""
                content += cont

                if (i < list.size - 1) {
                    content += ","
                }
            }
        } else {
            for (i in list.indices) {
                if (i >= 1) {
                    content += """
                    
                """
                }
                val nullable = list[i].nullable
                val cont = """
                    val ${list[i].name}: ${list[i].dataType}${columnQuestionMark(nullable)} = ${list[i].value}"""
                content += cont

                if (i < list.size - 1) {
                    content += ","
                }
            }
        }

        return content
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }
}