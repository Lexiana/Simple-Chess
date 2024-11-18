package main;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Classe représentant le plateau de jeu d'échecs. Elle gère le dessin des cases
 * du plateau.
 */
public class Board {

    // Nombre maximum de colonnes et de lignes sur le plateau
    final int MAX_COL = 8;
    final int MAX_ROW = 8;

    // Taille des cases du plateau
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    /**
     * Dessine le plateau de jeu sur le graphique fourni.
     *
     * @param g2 L'objet Graphics2D utilisé pour dessiner le plateau
     */
    public void draw(Graphics2D g2) {
        // Utilisation d'une variable pour alterner les couleurs des cases
        boolean isLightSquare = true;

        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                // Détermine la couleur de la case en fonction de sa position
                if (isLightSquare) {
                    g2.setColor(new Color(210, 165, 125)); // Couleur claire
                } else {
                    g2.setColor(new Color(175, 115, 70)); // Couleur foncée
                }

                // Dessine la case
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                // Alterne la couleur pour la prochaine case dans la même ligne
                isLightSquare = !isLightSquare;
            }
            // Alterne la couleur pour commencer la nouvelle ligne
            isLightSquare = !isLightSquare;
        }
    }
}
