package com.example.demo.action

import com.example.demo.inputDialog.generate.InputDFlow
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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*

class ActCreateFlow : AnAction() {
    private lateinit var project: Project

    override fun actionPerformed(event: AnActionEvent) {
        project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return

        val selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (selectedFile != null) {
            val directoryPath = selectedFile.path
            val virtualFile = LocalFileSystem.getInstance().findFileByPath(directoryPath) ?: return
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return
            val caretOffset = editor.caretModel.offset
            val insertionPoint = findInsertionPointByCaret(editor, psiFile) ?: return
            //val listField = getListNameFields(caretOffset, psiFile)

            // Відображаємо діалогове вікно для вводу параметрів
            val inputDialog = InputDFlow(directoryPath, caretOffset, psiFile, project)
            if (inputDialog.showAndGet()) {
                val filePath = inputDialog.getSelectePath() // Шлях до файлу у вигляді String

                val virtualFileSelected = LocalFileSystem.getInstance().findFileByPath(filePath) ?: return
                val psiFileSelected = PsiManager.getInstance(project).findFile(virtualFileSelected) ?: return


                val classBody = findContainingClass(psiFileSelected, caretOffset)
                if (classBody == null) {
                    project.showNotification("Class not found!")
                    return
                }

                // Позиція курсора
                val content = generateFlowObservationExpressions(
                    inputDialog.getSelecteViewModel(),
                    inputDialog.getName(),
                    inputDialog.getDispatchers()
                )

                val newFields = generateFlowFields(
                    inputDialog.getName(),
                    inputDialog.getType(),
                    inputDialog.getStructure(),
                    inputDialog.getStructureType()
                )

                // Виконуємо вставку коду в місці курсора
                ApplicationManager.getApplication().runWriteAction {
                    WriteCommandAction.runWriteCommandAction(project) {
                        //document.insertString(offset, content)
                        content.forEach { expression ->
                            insertionPoint.parent.addAfter(expression, insertionPoint)
                        }
                    }
                    WriteCommandAction.runWriteCommandAction(project) {
                        val anchor = findInsertionPointForFields(classBody)
                        if (anchor != null && anchor.parent != classBody.body) {
                            throw IllegalStateException("Anchor is not part of the same parent!")
                        }
                        //project.showNotification("Anchor is not part of the same parent! ${anchor!!.parent} | ${classBody.body}")
                        newFields.forEach { field ->
                            classBody.body?.addAfter(field, anchor)
                        }
                    }
                }

            }

        }
    }

    private fun findInsertionPointByCaret(editor: Editor, psiFile: PsiFile): PsiElement? {
        val caretOffset = editor.caretModel.offset
        val elementAtCaret = psiFile.findElementAt(caretOffset) ?: return null

        // Пошук методу, в якому знаходиться курсор
        val containingFunction = PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java)
        if (containingFunction != null) {
            val body = containingFunction.bodyBlockExpression // Отримуємо тіло методу (KtBlockExpression)
            return if (body != null && caretOffset in body.textRange.startOffset..body.textRange.endOffset) {
                // Курсор знаходиться в межах тіла методу
                elementAtCaret
            } else {
                // Курсор не знаходиться в тілі методу
                showHintMessage(editor, "Code can only be inserted inside a method body!")
                null
            }
        }

        // Якщо курсор поза межами будь-якого методу
        showHintMessage(editor, "Code can only be inserted inside a method!")
        return null
    }

    private fun showHintMessage(editor: Editor, message: String) {
        HintManager.getInstance().showErrorHint(editor, message)
    }

    // Знайти клас, в якому знаходиться курсор
    private fun findContainingClass(psiFile: PsiFile, offset: Int): KtClass? {
        val elementAtCaret = psiFile.findElementAt(offset)
        return PsiTreeUtil.getParentOfType(elementAtCaret, KtClass::class.java)
    }

    // Знайти правильну точку для вставки (останнє поле класу)
    private fun findInsertionPointForFields(ktClass: KtClass): PsiElement? {
        // Отримуємо всі декларації класу і фільтруємо тільки поля
        val fields = ktClass.declarations.filterIsInstance<KtProperty>()

        // Повертаємо останнє поле або ліву фігуруючу дужку (LBrace), якщо поля відсутні
        return fields.lastOrNull() ?: ktClass.body?.lBrace
    }

    private fun generateFlowObservationExpressions(
        selecteViewModel: String,
        name: String,
        dispatchers: String
    ): List<KtExpression> {
        val psiFactory = KtPsiFactory(project)

        val observationExpression = psiFactory.createExpression(
            """
        viewModel.viewModelScope.launch(Dispatchers.$dispatchers) {
          $selecteViewModel.$name 
                //.map { $name -> $name.map { it.name } }
                //.debounce(3000)
                //.filter { $name -> $name.any { it.name!!.startsWith("r") } }
                .collect { filteredList ->
                    // Continuation of your code
                }
        }
        """.trimIndent()
        )

        return listOf(observationExpression)
    }

    private fun generateFlowFields(
        name: String,
        type: String,
        structure: Boolean,
        structureType: String
    ): List<KtProperty> {
        val psiFactory = KtPsiFactory(project)

        val t = if (structure) {
            "$structureType<$type>"
        } else {
            type
        }

        val field1 = psiFactory.createDeclaration<KtProperty>(
            "val $name: Flow<$t> = _$name.asSharedFlow()"
        )

        val field2 = psiFactory.createDeclaration<KtProperty>(
            "private var _$name = MutableSharedFlow<$t>(replay = 1)"
        )

        return listOf(field1, field2)
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Database Plugin Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Generate LiveData", message, NotificationType.INFORMATION).notify(this)
    }
}