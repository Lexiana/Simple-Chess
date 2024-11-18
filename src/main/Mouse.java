package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classe pour gérer les événements de la souris dans le jeu. Elle étend
 * MouseAdapter pour simplifier la gestion des événements de souris.
 */
public class Mouse extends MouseAdapter {

    // Coordonnées de la souris
    public int x, y;

    // Indique si le bouton de la souris est enfoncé
    public boolean pressed;

    /**
     * Méthode appelée lorsque le bouton de la souris est enfoncé.
     *
     * @param e L'événement de souris contenant des informations sur l'état de
     * la souris
     */
    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true; // Met à jour l'état à "enfoncé"
    }

    /**
     * Méthode appelée lorsque le bouton de la souris est relâché.
     *
     * @param e L'événement de souris contenant des informations sur l'état de
     * la souris
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false; // Met à jour l'état à "relâché"
    }

    /**
     * Méthode appelée lorsque la souris est déplacée tout en maintenant un
     * bouton enfoncé.
     *
     * @param e L'événement de souris contenant des informations sur la position
     * actuelle de la souris
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e); // Met à jour les coordonnées de la souris
    }

    /**
     * Méthode appelée lorsque la souris est déplacée sans boutons enfoncés.
     *
     * @param e L'événement de souris contenant des informations sur la position
     * actuelle de la souris
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e); // Met à jour les coordonnées de la souris
    }

    /**
     * Met à jour les coordonnées x et y de la souris.
     *
     * @param e L'événement de souris contenant les nouvelles coordonnées
     */
    private void updateMousePosition(MouseEvent e) {
        x = e.getX(); // Récupère la coordonnée x
        y = e.getY(); // Récupère la coordonnée y
    }
}
