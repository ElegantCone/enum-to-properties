package ru.gayn.plugin;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class DocumentationPopup {
    private static Balloon currentBalloon;
    private static PsiElement currentElement;

    public static void showBalloonHintAtMouse(Editor editor, String documentationText, MouseEvent mouseEvent, PsiElement element) {
        if (element.equals(currentElement) && currentBalloon != null) {
            return;
        }
        if (currentBalloon != null) {
            currentBalloon.dispose();
        }
        JLabel label = new JLabel("<html>" + documentationText.replace("\n", "<br>") + "</html>");
        Balloon balloon = JBPopupFactory.getInstance().createBalloonBuilder(label).setFillColor(JBColor.LIGHT_GRAY).createBalloon();
        Point mousePosition = mouseEvent.getPoint();
        RelativePoint relativePoint = new RelativePoint(editor.getContentComponent(), mousePosition);
        balloon.show(relativePoint, Balloon.Position.below);
        currentElement = element;
        currentBalloon = balloon;
    }
}
