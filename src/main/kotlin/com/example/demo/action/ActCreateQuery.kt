package com.example.demo.action

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.inputDialog.generate.InputDQuery
import com.example.demo.model.Query
import com.example.demo.model.QueryType
import com.intellij.codeInsight.hint.HintManager
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*
import javax.swing.DefaultListModel
import javax.swing.JList

class ActCreateQuery : AnAction() {
    private lateinit var project: Project

    override fun actionPerformed(event: AnActionEvent) {
        project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return

        val selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (selectedFile != null) {
            val directoryPath = selectedFile.path
            val virtualFile = LocalFileSystem.getInstance().findFileByPath(directoryPath) ?: return
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return
            val ktFile = psiFile as? KtFile
            val insertionPoint = findInsertionPointByCaret(editor, psiFile) ?: return

            val listModelQuery = DefaultListModel<Query>()
            val listQuery = JList(listModelQuery)

            // Відображаємо діалогове вікно для вводу параметрів
            val inputDialog = InputDQuery(listQuery, directoryPath, project)
            if (inputDialog.showAndGet()) {
                val name = inputDialog.getName()
                val queryCategory = inputDialog.getQueryCategory()
                val queryType = inputDialog.getQueryType()
                val valueQuery = inputDialog.getValueQuery()
                val onConflict = inputDialog.getOnConflict()
                val selectedFilePathPackage = inputDialog.getSelectedFilePathPackage()
                val selectedClassName = inputDialog.getSelectedClassName()
                val column = inputDialog.getColumnSelected()
                val ktFileSelecte = inputDialog.getKtFileSelecte()

                val generatedImport = generateImport(selectedFilePathPackage) // Генеруємо імпорт
                insertGeneratedImport(ktFile!!, editor, project, generatedImport) // Вставляємо його

                // Позиція курсора
                val content = generateLiveDataObservationExpressions(
                    Query(
                        queryCategory,
                        queryType,
                        name,
                        null,
                        onConflict,
                        valueQuery,
                        selectedFilePathPackage,
                        selectedClassName,
                        ktFileSelecte,
                        column
                    )
                )

                // Виконуємо вставку коду в місці курсора
                ApplicationManager.getApplication().runWriteAction {
                    WriteCommandAction.runWriteCommandAction(project) {
                        //document.insertString(offset, content)
                        content.forEach { expression ->
                            insertionPoint.parent.addAfter(expression, insertionPoint)
                        }
                    }
                }

            }
        }
    }

    private fun findInsertionPointByCaret(editor: Editor, psiFile: PsiFile): PsiElement? {
        val caretOffset = editor.caretModel.offset
        val elementAtCaret = psiFile.findElementAt(caretOffset) ?: return null

        // Перевірка, чи курсор знаходиться в методі
        val containingFunction = PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java)
        if (containingFunction != null) {
            val body = containingFunction.bodyBlockExpression // Отримуємо тіло методу (KtBlockExpression)
            return if (body != null && caretOffset in body.textRange.startOffset..body.textRange.endOffset) {
                // Курсор знаходиться в межах тіла методу
                elementAtCaret
            } else {
                // Курсор не знаходиться в тілі методу
                showHintMessage(editor, "Code can only be inserted into the interface body!")
                null
            }
        }

        // Перевірка, чи курсор знаходиться в інтерфейсі
        val containingInterface = PsiTreeUtil.getParentOfType(
            elementAtCaret,
            KtClass::class.java,
            false // strict = false, тобто не обов'язково прямий батько
        )?.takeIf { it.isInterface() }

        if (containingInterface != null) {
            // Перевірка, чи курсор знаходиться в правильній позиції в інтерфейсі
            val body = containingInterface.body // Отримуємо тіло інтерфейсу
            return if (body != null && caretOffset in body.textRange.startOffset..body.textRange.endOffset) {
                // Курсор знаходиться в межах тіла інтерфейсу, але не всередині сигнатури іншого методу
                val containingMethod = PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java)
                if (containingMethod == null) {
                    // Курсор не всередині сигнатури іншого методу
                    elementAtCaret
                } else {
                    showHintMessage(editor, "Code cannot be inserted inside a method signature!")
                    null
                }
            } else {
                // Курсор не в межах тіла інтерфейсу
                showHintMessage(editor, "Code can only be inserted into the interface body!")
                null
            }
        }

        // Якщо курсор поза межами методу чи інтерфейсу
        showHintMessage(editor, "Code can only be inserted inside a method or an interface!")
        return null
    }

    private fun showHintMessage(editor: Editor, message: String) {
        HintManager.getInstance().showErrorHint(editor, message)
    }

    private fun generateLiveDataObservationExpressions(query: Query): List<KtExpression> {
        val psiFactory = KtPsiFactory(project)

        var content = ""

        var cont = ""
        val typeQuery = query.queryCategory
        val queryType = query.queryType
        val name = query.name
        val nameFile = query.nameChooseFile
        val onConflict = query.onConflict
        val columnSelect = query.columnSelect
        val ktFile = query.ktFile

        if (queryType == QueryType.TEMPLATE) {
            cont = when (typeQuery) {
                "AllEntity" -> {
                    val rawTableName = findEntityTableName(ktFile, nameFile).trim()
                        .replace("\"", "") // Видаляємо зайві лапки, якщо є
                    val tableName = CapitalizeFirstLetter().lowercaseChar(rawTableName) //
                    //val tableName = CapitalizeFirstLetter().lowercaseChar(findEntityTableName(ktFile, name))
                    """
                    @Query("SELECT * FROM $tableName")
                    fun $name(): List<${CapitalizeFirstLetter().uppercaseChar(nameFile)}>"""
                }

                "ListEntitiesEmpty" -> {
                    val rawTableName = findEntityTableName(ktFile, nameFile).trim()
                        .replace("\"", "") // Видаляємо зайві лапки, якщо є
                    val tableName = CapitalizeFirstLetter().lowercaseChar(rawTableName)

                    """
                    @Query("SELECT * from $tableName LIMIT 1")
                    fun $name(): ${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    }?"""
                }

                "SearchByLetter" -> {
                    val raw = findEntityTableName(ktFile, nameFile).trim()
                        .replace("\"", "") // Видаляємо зайві лапки, якщо є
                    val tableName = CapitalizeFirstLetter().lowercaseChar(raw)
                    val columnName = columnSelect!!

                    """
                    @Query("SELECT * FROM $tableName WHERE $columnName LIKE '%' || :${
                        CapitalizeFirstLetter().lowercaseChar(
                            columnName
                        )
                    } || '%'")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(columnName)}: String): List<${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    }>"""
                }

                "Delete" -> """
                    @$typeQuery(entity = ${CapitalizeFirstLetter().uppercaseChar(nameFile)}::class)
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: ${
                    CapitalizeFirstLetter().uppercaseChar(
                        nameFile
                    )
                })"""
                // Other options to choose from
                else -> """
                    @$typeQuery(entity = ${CapitalizeFirstLetter().uppercaseChar(nameFile)}::class${
                    if (onConflict != null) {
                        ", onConflict = OnConflictStrategy.${onConflict}"
                    } else {
                        ""
                    }
                })
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: ${
                    CapitalizeFirstLetter().uppercaseChar(
                        nameFile
                    )
                })"""
            }

        } else {
            val valueQuery = query.valueQuery
            cont = """
                    @$typeQuery("$valueQuery")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: ${
                CapitalizeFirstLetter().uppercaseChar(
                    query.nameChooseFile
                )
            })"""
        }

        content += cont


        val observationExpression = psiFactory.createExpression(
            content.trimIndent()
        )

        return listOf(observationExpression)
    }

    private fun generateImport(selectedFilePathPackage: String): String {
        return selectedFilePathPackage.ifEmpty { "" }
    }


    private fun insertGeneratedImport(ktFile: KtFile, editor: Editor, project: Project, generatedImport: String) {
        // Знаходимо список імпортів
        WriteCommandAction.runWriteCommandAction(project) {
            val importList: KtImportList? = ktFile.importList

            // Якщо список імпортів уже існує
            if (importList != null) {
                val existingImports = importList.imports.map { it.importedFqName?.asString() }
                //project.showNotification("Existing imports: $existingImports")
                // Перевіряємо, чи імпорт уже є в списку
                if (existingImports.contains(generatedImport.trim())) {
                    //project.showNotification("Import already exists: $generatedImport")
                    return@runWriteCommandAction // Якщо імпорт існує, нічого не додаємо
                }

                val document = editor.document
                val psiDocumentManager = PsiDocumentManager.getInstance(project)
                psiDocumentManager.commitDocument(document) // Комітимо поточний стан документа

                // Отримуємо позицію після останнього імпорту
                val lastImportOffset = importList.textRange.endOffset

                // Вставляємо імпорт після останнього імпорту
                val import = """import $generatedImport"""
                document.insertString(lastImportOffset, "\n$import")

                // Оновлюємо PSI дерево
                psiDocumentManager.commitDocument(document)
            } else {
                val document = editor.document
                document.insertString(0, "$generatedImport\n\n")
                PsiDocumentManager.getInstance(project).commitDocument(document)
            }
        }
    }


    private fun findEntityTableName(ktFile: KtFile?, nameClass: String): String {
        // Обходимо всі декларації у файлі
        if (ktFile != null) {
            ktFile.declarations.forEach { declaration ->
                // Приведення до KtModifierListOwner для доступу до анотацій
                val annotations = (declaration as? KtModifierListOwner)?.annotationEntries ?: return@forEach

                // Шукаємо анотацію @Entity
                annotations.forEach { annotation ->
                    val annotationName = annotation.shortName?.asString()
                    if (annotationName == "Entity") {
                        // Знаходимо атрибут tableName
                        annotation.valueArguments.forEach { argument ->
                            val name = argument.getArgumentName()?.asName?.asString()
                            return if (name == "tableName") {
                                argument.getArgumentExpression()?.text.toString() // Повертаємо знайдене значення tableName
                            } else {
                                nameClass
                            }
                        }
                    }
                }
            }
            return nameClass
        } else {
            return nameClass
        }
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Database Plugin Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Generate Query", message, NotificationType.INFORMATION).notify(this)
    }
}