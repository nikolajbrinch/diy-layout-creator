package org.diylc.application

import groovy.transform.CompileStatic

import java.awt.Dimension
import java.awt.EventQueue
import java.awt.event.ActionEvent

import javax.swing.AbstractAction
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Lazy
@CompileStatic
@Scope(value = "window")
class DiylcFrame extends JFrame {

    DiylcModel model

    DiylcController controller

    DiylcView view

    @Autowired
    WindowManager windowManager

    @Autowired
    public DiylcFrame(ApplicationContext applicationContext) {
        super('Title')

        model = applicationContext.getAutowireCapableBeanFactory().createBean(DiylcModel.class)
        controller = applicationContext.getAutowireCapableBeanFactory().createBean(DiylcController.class)
        controller.model = model
        view = applicationContext.getAutowireCapableBeanFactory().createBean(DiylcView.class)
        view.controller = controller

        setPreferredSize(new Dimension(800, 600))
        JMenuBar menuBar = new JMenuBar()
        JMenu menu = new JMenu()
        menu.add(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        DiylcFrame frame = windowManager.newWindow()
                        EventQueue.invokeLater(new Runnable() {
                                    public void run() {
                                        frame.setVisible(true)
                                    }
                                })
                    }
                })
        menuBar.add(menu)
        setJMenuBar(menuBar)
        pack()
    }
}
