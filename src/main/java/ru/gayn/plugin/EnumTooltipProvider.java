package ru.gayn.plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

public class EnumTooltipProvider implements EditorMouseMotionListener {
    @Override
    public void mouseMoved(EditorMouseEvent event) {
        Editor editor = event.getEditor();
        Project project = event.getEditor().getProject();
        if (project == null) return;
        int offset = editor.logicalPositionToOffset(editor.xyToLogicalPosition(event.getMouseEvent().getPoint()));
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) return;
        PsiElement element = psiFile.findElementAt(offset);
        if (element == null) return;
        var parent = element.getParent();
        if (parent instanceof PsiReferenceExpression) {
            parent = ((PsiReferenceExpression) parent).resolve();
        }
        if (parent instanceof PsiEnumConstant enumConstant) {
            var containingClass = enumConstant.getContainingClass();
            if (containingClass == null) return;
            String enumClassName = containingClass.getName();
            String enumConstantName = enumConstant.getName().toLowerCase();
            var key = enumClassName + "." + enumConstantName;
            var module = ModuleUtilCore.findModuleForPsiElement(containingClass);
            var message = getMessage(module, key);
            if (message == null) return;
            DocumentationPopup.showBalloonHintAtMouse(editor, message, event.getMouseEvent(), element);
        }
    }

    private String getMessage(Module module, String key) {
        StringBuilder result = new StringBuilder();
        var propertiesFileType = FileTypeManager.getInstance().getFileTypeByExtension("properties");
        var propertyFiles = FileTypeIndex.getFiles(propertiesFileType, GlobalSearchScope.moduleScope(module));
        try {
            for (var file : propertyFiles) {
                Properties properties = new Properties();
                InputStreamReader isr = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                properties.load(isr);
                if (properties.containsKey(key)) {
                    result.append(file.getName()).append(": ").append(properties.getProperty(key)).append("\n");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return result.isEmpty() ? null : result.toString();

    }

}
