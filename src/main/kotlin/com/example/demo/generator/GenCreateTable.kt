package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.*
import com.example.demo.tableConfig.TableCreate
import javax.swing.DefaultListModel

class GenCreateTable(
    private val entityAttribute: EntityAdvancedSettings?,
    private val isEntity: Boolean,
    private val isColumnInfo: Boolean,
    private val isForeignKey: Boolean,
    private val isIndex: Boolean
) {
    private var code = ""

    fun generate(
        config: TableCreate,
        listModelColumnsAttribute: DefaultListModel<ColumnAdvancedSettings?>,
        listModelForeignKeysAttribute: DefaultListModel<ForeignKey?>,
        listModelIndexesAttribute: DefaultListModel<Index?>
    ): String {
        code = """
                package ${config.getPath()}

                import androidx.room.Entity
                import androidx.room.PrimaryKey
                ${if (isColumnInfo) "import androidx.room.ColumnInfo" else ""}
                ${if (isForeignKey) "import androidx.room.ForeignKey" else ""}
                ${if (isIndex) "import androidx.room.Index" else ""}

                ${generateEntity(listModelForeignKeysAttribute, listModelIndexesAttribute)}
                data class ${CapitalizeFirstLetter().uppercaseChar(config.getTableName())}(
                ${generateColumns(config.getListColumnData(), listModelColumnsAttribute)}
                ) {
                    ${generatePrimaryKeys(config.getListPrimaryKeyData())}       
                }
            """.trimIndent()

        return code
    }

    private fun generateEntity(
        listModelForeignKeysAttribute: DefaultListModel<ForeignKey?>,
        listModelIndexesAttribute: DefaultListModel<Index?>
    ): String {
        var content = ""

        if (isEntity) {
            content = """
                @Entity(${
                if (entityAttribute!!.tableName != null)
                    "\n                    tableName = \"${entityAttribute.tableName}\","
                else ""
            }${
                if (entityAttribute.primaryKeys != null)
                    "\n                    primaryKeys = [${getPrimaryKeys()}" + 
                            "\n                    ],"
                else ""
            }
            ${
                if (isForeignKey)
                    generateForeignKeys(listModelForeignKeysAttribute)
                else ""
            }
            ${
                if (isIndex)
                    "\n                    indices = [\n" +
                            "                        Index(\n" +
                            "                            name = \"index_author_and_title\",\n" +
                            "                            value = [\"authorId\"],\n" +
                            "                            orders = [Index.Order.ASC],\n" +
                            "                            unique = true\n" +
                            "                        ),\n" +
                            "                        Index(value = [\"title\"])\n" +
                            "                    ],"
                else ""
            }
            ${
                if (entityAttribute.inheritSuperIndices != null)
                    "        inheritSuperIndices = ${entityAttribute.inheritSuperIndices},"
                else ""
            }
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

    private fun generateForeignKeys(list: DefaultListModel<ForeignKey?>): String {
        var content = """        foreignKeys = ["""

        list.elements().toList().forEachIndexed { i, element ->
            val entity = element!!.entity
            val parentColumns = element.parentColumns
            val childColumns = element.childColumns
            val onDelete = element.onDelete
            val onUpdate = element.onUpdate
            val deferred = element.deferred

            val cont = """    
                                         ForeignKey(
                                                    ${
                                                       if (entity != null) {
                    "        entity = $entity::class,\n"
                                                       } else {
                    ""
                                                       }
                                                     }
                                                     ${
                if (parentColumns != null) {
                    "        parentColumns = [" +
                            "${parentColumns.forEach { e -> "${e}" }
                            }],\n"
                } else {
                    ""
                }
            }
            ${
                if (childColumns != null) {
                    "        childColumns = [" +
                            "${childColumns.forEach { e -> "${e}" }
                            }],\n"
                } else {
                    ""
                }
            }
            ${
                if (onDelete != null) {
                    "        onDelete = ForeignKey.$onDelete,\n"
                } else {
                    ""
                }
            }
            ${
                if (onUpdate != null) {
                    "        onUpdate = ForeignKey.$onUpdate,\n"
                } else {
                    ""
                }
            }
            ${
                if (entity != null && deferred != null) {
                    "        deferred = $deferred\n"
                } else {
                    ""
                }
            }                    )"""
            content += cont

            if (i < list.size - 1) {
                content += ","
            }
        }
        content += "                    ],"


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