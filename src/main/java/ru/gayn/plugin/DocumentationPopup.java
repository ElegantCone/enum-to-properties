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

    private static Balloon currentBalloon; // Хранение текущей подсказки
    private static PsiElement currentElement; // Хранение текущего элемента


    public static void showBalloonHintAtMouse(Editor editor, String documentationText, MouseEvent mouseEvent, PsiElement element) {
        if (element.equals(currentElement) && currentBalloon != null) {
            return; // Подсказка уже показывается для этого элемента
        }
        // Если есть активная подсказка, скрываем её
        if (currentBalloon != null) {
            currentBalloon.dispose();
        }

        // Создаем содержимое всплывающего окна
        JLabel label = new JLabel("<html>" + documentationText.replace("\n", "<br>") + "</html>");
        label.setBorder(BorderFactory.createLineBorder(JBColor.GRAY));

        // Создаем Balloon с помощью JBPopupFactory
        Balloon balloon = JBPopupFactory.getInstance()
                .createBalloonBuilder(label)
                .setFillColor(JBColor.LIGHT_GRAY)
                .createBalloon();

        // Получаем позицию мыши относительно компонента редактора
        Point mousePosition = mouseEvent.getPoint();
        RelativePoint relativePoint = new RelativePoint(editor.getContentComponent(), mousePosition);

        // Показываем Balloon в позиции мыши
        balloon.show(relativePoint, Balloon.Position.below);

        // Сохраняем ссылку на текущий элемент и подсказку
        currentElement = element;
        currentBalloon = balloon;
    }
}
